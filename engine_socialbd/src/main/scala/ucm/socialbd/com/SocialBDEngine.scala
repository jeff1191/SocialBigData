package ucm.socialbd.com

import org.apache.flink.streaming.api.scala.DataStream
import ucm.socialbd.com.config.SocialBDProperties
import ucm.socialbd.com.process._
import ucm.socialbd.com.sinks.WriterSinkStream

/**
  * Created by Jeff on 15/04/2017.
  */
object SocialBDEngine {
 private val streamNames = Set("AIR", "TRAFFIC", "TWITTER", "BICIMAD", "EMTBUS")

  def main(args: Array[String]): Unit = {
    println("-------------------------")
    println("  SocialBigData-CM Engine")
    println("-------------------------")
    if (args.length !=  2 || !checkExtFile("conf",args(1))) printUsage(exit = true)

    val socialBDProperties = new SocialBDProperties(args(1))
    val streamTransformation = args(0).trim.toUpperCase match {
      case "AIR" => new AirStream(socialBDProperties)
      case "TRAFFIC" => new TrafficStream(socialBDProperties)
      case "TWITTER" => new TwitterStream(socialBDProperties)
      case "BICIMAD" => new BiciMadStream(socialBDProperties)
      case "EMTBUS" => new EMTBusStream(socialBDProperties)
      case _ => {
        println (s"Unrecognized stream type ${args(0)}")
        printUsage(exit = false)
        sys.exit(1)
      }
    }
    streamTransformation.process
  }

  def printUsage(exit: Boolean = false): Unit = {
    println ("Arguments: <stream type> <engine_socialbd.conf> ")
    println ("stream type must be one of: [" + streamNames.mkString(", ") +"]")
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
