package ucm.socialbd.com.serde


import ucm.socialbd.com.dataypes.EnrichmentModel.{EAir, ETweet}
import ucm.socialbd.com.dataypes.EnrichmentObj
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.commons.lang3.StringUtils
import java.nio.charset.StandardCharsets

import net.liftweb.json.JsonParser
import org.json4s.native.JsonMethods._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.apache.flink.api.common.serialization.DeserializationSchema
class JsonToObject[T: Manifest] (clazz: Class[T]) extends DeserializationSchema[T ]{


  override def isEndOfStream(t: T) = false

  override def deserialize(bytes: Array[Byte]) = {
    implicit val formats = net.liftweb.json.DefaultFormats
    JsonParser.parse(
      StringUtils.toEncodedString(bytes, StandardCharsets.UTF_8)
    ).extract[T]
  }

  override def getProducedType = TypeExtractor.getForClass(clazz)
}
