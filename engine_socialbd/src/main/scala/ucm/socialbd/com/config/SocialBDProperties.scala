package ucm.socialbd.com.config

import java.io.File

import com.typesafe.config.ConfigFactory
/**
  * Created by Jeff on 16/04/2017.
  */

case class TwitterConf(twitterTopicIn: String, twitterTopicOut:String,elasticIndex:String,elasticType:String, outputDir:String)

case class TrafficConf(urbanTrafficTopicIn:String, interUrbanTrafficTopicIn:String, trafficTopicOut: String,elasticIndex:String,elasticType:String, outputDir:String)

case class QualityAirConf(qualityAirTopicIn:String, qualityAirTopicOut:String,elasticIndex:String,elasticType:String, outputDir:String)

case class BiciMADConf(bicimadTopicIn:String, bicimadTopicOut:String,elasticIndex:String,elasticType:String, outputDir:String)

case class EMTBusConf(emtbusTopicIn:String, emtbusTopicOut:String,elasticIndex:String,elasticType:String, outputDir:String)

@SerialVersionUID(100L)
class SocialBDProperties(path:String) extends Serializable{

  val twitterConf = TwitterConf(conf.getString("twitter.twitterTopicIn"),
                    conf.getString("twitter.twitterTopicOut"),
                    conf.getString("twitter.elasticIndex"),
                    conf.getString("twitter.elasticType"),
                    conf.getString("twitter.outputDir"))

  //checks about if exist components in conf file
  conf.checkValid(ConfigFactory.defaultReference(), "twitter")
  conf.checkValid(ConfigFactory.defaultReference(), "traffic")
  conf.checkValid(ConfigFactory.defaultReference(), "qualityAir")
  conf.checkValid(ConfigFactory.defaultReference(), "bicimad")
  conf.checkValid(ConfigFactory.defaultReference(), "emtbus")
  conf.checkValid(ConfigFactory.defaultReference(), "kafkaBrokersUrls")
  conf.checkValid(ConfigFactory.defaultReference(), "zkUrl")
  conf.checkValid(ConfigFactory.defaultReference(), "elasticClusterName")
  conf.checkValid(ConfigFactory.defaultReference(), "elasticNodeName")
  conf.checkValid(ConfigFactory.defaultReference(), "elasticPort")
  conf.checkValid(ConfigFactory.defaultReference(), "elasticUrl")
 val trafficConf = TrafficConf(conf.getString("traffic.urbanTrafficTopicIn"),
                   conf.getString("traffic.interUrbanTrafficTopicIn"),
                   conf.getString("traffic.trafficTopicOut"),
                   conf.getString("traffic.elasticIndex"),
                   conf.getString("traffic.elasticType"),
                   conf.getString("traffic.outputDir"))
  val qualityAirConf = QualityAirConf(conf.getString("qualityAir.qualityAirTopicIn"),
                  conf.getString("qualityAir.qualityAirTopicOut"),
                  conf.getString("qualityAir.elasticIndex"),
                  conf.getString("qualityAir.elasticType"),
                  conf.getString("qualityAir.outputDir"))
  val biciMADConf = BiciMADConf(conf.getString("bicimad.bicimadTopicIn"),
                  conf.getString("bicimad.bicimadTopicOut"),
                  conf.getString("bicimad.elasticIndex"),
                  conf.getString("bicimad.elasticType"),
                  conf.getString("bicimad.outputDir"))
  val eMTBusConf = EMTBusConf(conf.getString("emtbus.emtbusTopicIn"),
                  conf.getString("emtbus.emtbusTopicOut"),
                  conf.getString("emtbus.elasticIndex"),
                  conf.getString("emtbus.elasticType"),
                  conf.getString("emtbus.outputDir"))
  val kafkaBrokersUrls =  conf.getString("kafkaBrokersUrls")
  val zkUrl =  conf.getString("zkUrl")
  val elasticClusterName =  conf.getString("elasticClusterName")
  val elasticNodeName =  conf.getString("elasticNodeName")
  val elasticPort =  conf.getInt("elasticPort")
  val elasticUrl =  conf.getString("elasticUrl")
  val outputMode =  conf.getString("outputMode")
  private val conf = ConfigFactory.parseFile(new File(path))
}
