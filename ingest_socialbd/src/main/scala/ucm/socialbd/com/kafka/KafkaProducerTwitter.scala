package ucm.socialbd.com.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer
import ucm.socialbd.com.config.{IngestSBDProperties}
import ucm.socialbd.com.utils.{SocialBDConfig, TwitterUtils}

/**
  * Created by Jeff on 09/03/2017.
  */
class KafkaProducerTwitter(socialBDProperties: IngestSBDProperties) extends KafkaProducerActions{
  //keep in mind (longitud, latitud) // it should be in a config.file or config.json
  val madrid = Array(-3.7037901999999576 - .5, 40.4167754- .5)
  val madrid1 = Array(-3.7037901999999576 + .5, 40.4167754 + .5)
  val barcelona = Array(2.1734034999999494 -.5, 41.3850639 -.5)
  val barcelona1 = Array(2.1734034999999494 + .5, 41.3850639 + .5)
  val mexicodf = Array( -99.13320799999997 - .5, 19.4326077 - .5)
  val mexicodf1 = Array( -99.13320799999997 + .5, 19.4326077 + .5)
  val bogota = Array(-74.072092 - .5,4.710988599999999 - .5)
  val bogota1 = Array(-74.072092 + .5,4.710988599999999 + .5)
  val buenosaires = Array(-58.381559100000004 - .5,-58.381559100000004 - .5)
  val buenosaires1 = Array(-58.381559100000004 + .5,-58.381559100000004 + .5)
  val santiagochile = Array(-70.65044920000003 - .5,-33.4378305 - .5)
  val santiagochile1 = Array(-70.65044920000003 + .5,-33.4378305 + .5)
  val montevideo = Array(-56.16453139999999 - .5,-34.9011127 - .5)
  val montevideo1 = Array(-56.16453139999999 + .5,-34.9011127 + .5)
  val lima = Array(-76.27108329999999 - .5,-12.2720956- .5)
  val lima1 = Array(-76.27108329999999 + .5,-12.2720956 + .5)
  val caracas = Array(-66.90360629999998 - .5,10.4805937 - .5)
  val caracas1 = Array(-66.90360629999998 + .5,10.4805937 + .5)
  val quito = Array(-78.46783820000002 - .5,-0.1806532 - .5)
  val quito1 = Array(-78.46783820000002 + .5,-0.1806532 + .5)
  val laPaz = Array(-68.11929359999999 - .5,-16.489689 - .5)
  val laPaz1 = Array(-68.11929359999999 + .5,-16.489689 + .5)
  val asuncion = Array(-57.57592599999998 - .5,-25.26373989999999 - .5)
  val asuncion1 = Array(-57.57592599999998 + .5,-25.26373989999999 + .5)
  val panama = Array(-79.51986959999999 - .5,8.982379199999999 - .5)
  val panama1 = Array(-79.51986959999999 + .5,8.982379199999999 + .5)
  val costarica = Array(-83.75342799999999 - .5,9.748916999999999 - .5)
  val costarica1 = Array(-83.75342799999999 + .5,9.748916999999999 + .5)
  val rdominicana = Array(-70.16265099999998 - .5,18.735693 - .5)
  val rdominicana1 = Array(-70.16265099999998 + .5,18.735693 + .5)

  override def process(): Unit = {
    val  props = SocialBDConfig.getProperties(socialBDProperties)

    val producer = new KafkaProducer[String, String](props)

    //configure twitter client
    val twitterConfig = TwitterUtils.config(
      socialBDProperties.twitterConf.consumerKey,
      socialBDProperties.twitterConf.consumerSecret,
      socialBDProperties.twitterConf.accessToken,
      socialBDProperties.twitterConf.accessSecret
    )
    //create a listener with a kafka-producer
    val twitterListener = TwitterUtils.simpleStatusListener(
      producer,
      socialBDProperties.twitterConf.twitterTopic
    )



    val filters= Array(
      mexicodf,
      mexicodf1,
      madrid,
      madrid1,
      barcelona,
      barcelona1,
      buenosaires,
      buenosaires1,
      santiagochile,
      santiagochile1,
      montevideo,
      montevideo1,
      lima,
      lima1,
      caracas,
      caracas1,
      quito,
      quito1,
      laPaz,
      laPaz1,
      asuncion,
      asuncion1,
      panama,
      panama1,
      costarica,
      costarica1,
      rdominicana,
      rdominicana1
    )

    //finally, create a stream using twitterConfig, twitterListener and the filters, its important consider that the
    //listener wraps a kafka-producer
    val stream = TwitterUtils.createStreamLocation(
      twitterConfig,
      twitterListener,
      filters
    )
  }
}
