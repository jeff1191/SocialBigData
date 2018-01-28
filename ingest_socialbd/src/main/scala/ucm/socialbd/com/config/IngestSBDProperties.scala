package ucm.socialbd.com.config
import java.io.File

import com.typesafe.config.ConfigFactory
/**
  * Created by Jeff on 16/04/2017.
  */

case class TwitterConf(twitterTopic: String, consumerKey:String,consumerSecret:String,accessToken:String,accessSecret:String)

case class TrafficConf(urbanTrafficTopic:String, interUrbanTrafficTopic:String,  urlTraffic: String, delay: Long)

case class QualityAirConf(qualityAirTopic:String, qualityAirUrl:String, delay: Long)

case class BiciMadConf(bicimadTopic: String, urlBiciMad:String, delayMinutes:Long)

case class EMTBusesConf(emtbusesTopic:String, urlEMTBuses:String, delayMinutes:Long )

class IngestSBDProperties(path:String) {
  
  val twitterConf = TwitterConf(conf.getString("twitter.twitterTopic"),
                    conf.getString("twitter.consumerKey"),
                    conf.getString("twitter.consumerSecret"),
                    conf.getString("twitter.accessToken"),
                    conf.getString("twitter.accessSecret"))
  //checks about if exist components in application.conf
  conf.checkValid(ConfigFactory.defaultReference(), "twitter")
  conf.checkValid(ConfigFactory.defaultReference(), "traffic")
  conf.checkValid(ConfigFactory.defaultReference(), "air")
  conf.checkValid(ConfigFactory.defaultReference(), "bici")
  conf.checkValid(ConfigFactory.defaultReference(), "emtbuses")
  conf.checkValid(ConfigFactory.defaultReference(), "kafkaUrl")
  conf.checkValid(ConfigFactory.defaultReference(), "zkUrl")
  val trafficConf = TrafficConf(conf.getString("traffic.urbanTrafficTopic"),
                    conf.getString("traffic.interUrbanTrafficTopic"),
                    conf.getString("traffic.urlTraffic"),
                    conf.getInt("traffic.delayMinutes") * 60 * 1000)
  val qualityAirConf = QualityAirConf(conf.getString("air.qualityAirTopic"),
                    conf.getString("air.qualityAirUrl"),
                    conf.getInt("air.delayMinutes") * 60 * 1000)
  val biciMadConf = BiciMadConf(conf.getString("bicimad.bicimadTopic"),
                    conf.getString("bicimad.urlBiciMad"),
                    conf.getInt("bicimad.delayMinutes") * 60 * 1000)
  val eMTBusesConf = EMTBusesConf(conf.getString("buses.emtbusesTopic"),
                    conf.getString("buses.urlEMTBuses"),
                    conf.getInt("buses.delayMinutes") * 60 * 1000)
  val urlKafka =  conf.getString("kafkaUrl")
  val urlZookeeper =  conf.getString("zkUrl")
  private val conf = ConfigFactory.parseFile(new File(path))
}
