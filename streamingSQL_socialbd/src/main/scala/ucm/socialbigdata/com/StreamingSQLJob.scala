package ucm.socialbigdata.com

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.{InetAddress, InetSocketAddress}
import java.util
import java.util.{Date, Properties}

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala.{DataStream, _}
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaConsumer011}
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.table.api.scala._
import org.apache.flink.table.expressions.ExpressionParser
import org.apache.flink.types.Row
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import ucm.socialbd.com.dataypes.EnrichmentModel.{EAir, ETweet}
import ucm.socialbd.com.dataypes.EnrichmentObj
import ucm.socialbd.com.dataypes.RawModel.{Air, GroupHour, InterUrbanTraffic}
import ucm.socialbd.com.serde.JsonToObject
import ucm.socialbd.com.sources.KafkaFactoryConsumer
import ucm.socialbigdata.com.config.StreamingSQLProperties
import ucm.socialbigdata.com.elasticsearch.SimpleElasticsearchSink
import ucm.socialbigdata.com.operations.RowToJSONMap

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
object StreamingSQLJob {

  def main(args: Array[String]) {
    if (!checkExtFile("conf",args(0))){
      println ("Arguments: <streamingSQL_socialbd.conf> ")
      System.exit(1)
    }

    val socialBDProperties = new StreamingSQLProperties(args(0))


    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv= TableEnvironment.getTableEnvironment(env)

    val properties = new Properties()
    // comma separated list of Kafka brokers
    properties.setProperty("bootstrap.servers", socialBDProperties.kafkaBrokersUrls)

    val dataStream : DataStream[ETweet] = env.addSource(new FlinkKafkaConsumer011[ETweet](
      socialBDProperties.topic, new JsonToObject[ETweet](classOf[ETweet]), properties))

    tableEnv.registerDataStream("etweets", dataStream, 'id_str , 'createdAt, 'Xcoord, 'Ycoord, 'place, 'text, 'user, 'retweeted)

    val query = socialBDProperties.query.replace("SELECT","select").replace("Select","select").replace("FROM","from").replace("From","from")


    val pattern = """select(.*)from""".r


    var fields = ""

    pattern.findAllIn(query).matchData foreach {
      m => fields = m.group(1)
    }

    val resultTable = tableEnv.sqlQuery(query).toRetractStream[Row]

    val jsonDataStream : DataStream[String] = resultTable.map(_._2).map(new RowToJSONMap(fields.split(",").toList))

    jsonDataStream.print()
    val config = getElasticConfiguration(socialBDProperties)

    val transports = new java.util.ArrayList[InetSocketAddress]
    transports.add(new InetSocketAddress(InetAddress.getByName(socialBDProperties.elasticUrl), socialBDProperties.elasticPort))


    jsonDataStream.addSink(new ElasticsearchSink(config, transports,
      new SimpleElasticsearchSink(socialBDProperties.elasticIndex,socialBDProperties.elasticType) ))
    // execute program
    env.execute("Flink SQL SocialBigData-CM")
  }

  def getElasticConfiguration(socialBDProperties: StreamingSQLProperties): util.Map[String, String] = {
    val config = new java.util.HashMap[String, String]
    config.put("bulk.flush.max.actions", "1")
    config.put("cluster.name", socialBDProperties.elasticClusterName)
    config.put("node.name", socialBDProperties.elasticNodeName)
    config
  }


  def checkExtFile(ext:String, filename:String): Boolean ={
    val pat = s"""(.*)[.](${ext})""".r

    filename match {
      case pat(fn,ex) => true
      case _ => false
    }
  }
}