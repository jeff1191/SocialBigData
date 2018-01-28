package ucm.socialbd.com

import ucm.socialbd.com.cluster.InitCluster
import ucm.socialbd.com.config.IngestSBDProperties
import ucm.socialbd.com.kafka._

/**
  * Created by Jeff on 16/04/2017.
  */

object SocialBDIngest {
  private val ingestNames = Set("AIR", "TRAFFIC", "TWITTER","BICIMAD", "EMTBUS")

  def main(args: Array[String]): Unit = {
    println("-------------------------")
    println("  IngestSocialBigData-CM")
    println("-------------------------")
    if (args.length !=  2 || !checkExtFile("conf",args(1))) printUsage(exit = true)
    val conf = new IngestSBDProperties(args(1))
    if(args(1).trim.toUpperCase.equals("LOCAL")){
     // InitCluster.run(conf)
    }

    val ingest = args(0).trim.toUpperCase match {
      case "AIR" => new KafkaProducerAirQuality(conf)
      case "TRAFFIC" => new KafkaProducerTraffic(conf)
      case "TWITTER" => new KafkaProducerTwitter(conf)
      case "BICIMAD" => new KafkaProducerBiciMAD(conf)
      case "EMTBUS" => new KafkaProducerEMTBuses(conf)
      case _ => {
        println (s"Unrecognized ingest type ${args(0)}")
        printUsage(exit = false)
        sys.exit(1)
      }
    }
    ingest.process()
  }

  def printUsage(exit: Boolean = false): Unit = {
    println ("Arguments: <ingest name> <ingest_socialbd.conf> ")
    println ("Ingest name must be one of: [" + ingestNames.mkString(", ") +"]")
    if (exit)
      sys.exit(1)
  }

  def checkExtFile(ext:String, filename:String): Boolean ={
    val pat = s"""(.*)[.](${ext})""".r

    filename match {
      case pat(fn,ex) => true
      case _ => false
    }
  }
}