package ucm.socialbd.com.sources

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import ucm.socialbd.com.config.SocialBDProperties
import ucm.socialbd.com.dataypes.{EnrichmentFileObj, EnrichmentObj, RawObj}
import ucm.socialbd.com.factory.{DataTypeFactory, Instructions}
import ucm.socialbd.com.utils.SocialBDConfig
import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.expressions.ExpressionParser
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.TableEnvironment
import org.apache.kafka.clients.consumer.ConsumerConfig
import twitter4j.TwitterObjectFactory
import ucm.socialbd.com.context.AppEnvironment
import ucm.socialbd.com.dataypes.EnrichmentModel.{EAir, ETraffic, ETweet}
import ucm.socialbd.com.dataypes.RawModel.{Twitter, TwitterUser}
import ucm.socialbd.com.serde.JsonToObject
/**
  * Created by Jeff on 15/04/2017.
  */
object KafkaFactoryConsumer {


  def getRawStream(env:StreamExecutionEnvironment, socialBDProperties: SocialBDProperties,ins: String): DataStream[RawObj] = {
    //common properties
    val properties = SocialBDConfig.getKafkaProperties(socialBDProperties)
    ins match {
      case Instructions.GET_RAW_AIR =>
        //properties.setProperty("auto.offset.reset", "earliest")
        val streamAirKafka: DataStream[String]= env.addSource(
          new FlinkKafkaConsumer010[String](socialBDProperties.qualityAirConf.qualityAirTopicIn, new SimpleStringSchema(), properties))

        streamAirKafka.map(x => DataTypeFactory.getRawObject(x,Instructions.CREATE_RAW_AIR))

      case Instructions.GET_RAW_URBAN_TRAFFIC =>
        val streamUrbanTrafficKafka: DataStream[String]= env.addSource(new FlinkKafkaConsumer010[String](
          socialBDProperties.trafficConf.urbanTrafficTopicIn, new SimpleStringSchema(), properties))

        streamUrbanTrafficKafka.map(x => DataTypeFactory.getRawObject(x,Instructions.CREATE_RAW_URBAN_TRAFFIC))

      case Instructions.GET_RAW_INTERURBAN_TRAFFIC =>
        val streamInterUrbanTrafficKafka: DataStream[String]= env.addSource(new FlinkKafkaConsumer010[String](
          socialBDProperties.trafficConf.interUrbanTrafficTopicIn, new SimpleStringSchema(), properties))

        streamInterUrbanTrafficKafka.map(x => DataTypeFactory.getRawObject(x,Instructions.CREATE_RAW_INTERURBAN_TRAFFIC))

      case Instructions.GET_RAW_TWITTER =>
        val streamTwitterKafka: DataStream[String]= env.addSource(new FlinkKafkaConsumer010[String](
        socialBDProperties.twitterConf.twitterTopicIn, new SimpleStringSchema(), properties))

        streamTwitterKafka.map(tweetjson => DataTypeFactory.getRawObject(tweetjson,Instructions.CREATE_RAW_TWITTER))
      case Instructions.GET_RAW_BICIMAD =>
        val streamBiciKafka: DataStream[String]= env.addSource(new FlinkKafkaConsumer010[String](
        socialBDProperties.biciMADConf.bicimadTopicIn, new SimpleStringSchema(), properties))

        streamBiciKafka.map(x => DataTypeFactory.getRawObject(x,Instructions.CREATE_RAW_BICIMAD))
      case Instructions.GET_RAW_EMTBUS =>
        val streamTEmtBusKafka: DataStream[String]= env.addSource(new FlinkKafkaConsumer010[String](
          socialBDProperties.eMTBusConf.emtbusTopicIn, new SimpleStringSchema(), properties))

        streamTEmtBusKafka.map(x => DataTypeFactory.getRawObject(x,Instructions.CREATE_RAW_EMTBUS))

      case _ =>  throw new Exception(s"Unknown kafka source ${ins}")
    }
  }
  def getEnrichmentStream( topic: String, kafkaBrokers: String, registeredAtTableEnv:Boolean = false, fromBeginning: Boolean =false) = {
    val properties = new Properties()
    // comma separated list of Kafka brokers
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers)
    if(fromBeginning)
      properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    topic match {

      case "eAirQuality" =>
        val kafkaSource = new FlinkKafkaConsumer010[EAir](
          topic, new JsonToObject[EAir](classOf[EAir]), properties)
        if(fromBeginning) kafkaSource.setStartFromEarliest()

        val dataStream : DataStream[EAir] = AppEnvironment.env.addSource(kafkaSource)
        if(registeredAtTableEnv)
          AppEnvironment.tableEnv
          .registerDataStream(topic, dataStream, 'estacion , 'codigoEst, 'Xcoord, 'Ycoord, 'fechaHora, 'magnitudNombre, 'magnitudCod, 'tecnicaNom, 'tecnicaCod, 'valor, 'nivelIntensidadTrafico)

        dataStream
      case "eTraffic" =>
        val kafkaSource = new FlinkKafkaConsumer010[ETraffic](
          topic, new JsonToObject[ETraffic](classOf[ETraffic]), properties)
        if(fromBeginning) kafkaSource.setStartFromEarliest()

        val dataStream : DataStream[ETraffic] = AppEnvironment.env.addSource(kafkaSource)
        if(registeredAtTableEnv)
          AppEnvironment.tableEnv
            .registerDataStream(topic, dataStream, 'idelem , 'identif, 'fechaHora, 'tipo_elem, 'intensidad, 'carga, 'vmed, 'error, 'Xcoord, 'Ycoord)

        dataStream
      case "eTweets" =>
        val kafkaSource = new FlinkKafkaConsumer010[ETweet](
          topic, new JsonToObject[ETweet](classOf[ETweet]), properties)
        if(fromBeginning) kafkaSource.setStartFromEarliest()

        val dataStream : DataStream[ETweet] = AppEnvironment.env.addSource(kafkaSource)
        if(registeredAtTableEnv){
          AppEnvironment.tableEnv
            .registerDataStream("eTweets", dataStream, 'id_str , 'createdAt, 'Xcoord, 'Ycoord, 'place, 'text, 'user, 'retweeted)

        }
        dataStream
      case _ =>  throw new Exception(s"Unknown kafka source ${topic}")
    }
  }
}
