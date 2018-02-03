package ucm.socialbd.com.utils

import twitter4j.{Status, TwitterObjectFactory}
import ucm.socialbd.com.dataypes.RawModel.{Twitter, TwitterUser}
import net.liftweb.json._
import net.liftweb.json.Extraction._
/**
  * Created by Jeff on 21/05/2017.
  */
object TwitterUtil {

  private implicit val formats = net.liftweb.json.DefaultFormats
  // TODO: this method must be edited, now it works basically
  def getTwitterRawObj(rawJSON:String): Twitter ={

    //BUG : createStatus doesn't be able to convert all strings in Status Object, therefore need us use json.liftweb utils
    val status = TwitterObjectFactory.createStatus(rawJSON) // <- this doesn't work correctly
    val jsonMap = parse(rawJSON)

    val rawTwitterObj =Twitter("Unknown","Unknown","Unknown",
      "Unknown","Unknown","Unknown",
      TwitterUser("Unknown","Unknown","Unknown","Unknown",
        "Unknown","Unknown"),
      "Unknown")

    val auxPlace = jsonMap \ "place" \ "fullName"

    rawTwitterObj.id_str = status.getId.toString

    rawTwitterObj.place = if(!auxPlace.getClass.getName.equalsIgnoreCase(JsonAST.JNothing.getClass.getName)) compactRender(auxPlace).toString
                          else "Unknown"

    rawTwitterObj.createdAt = compactRender(jsonMap \ "createdAt").toString
    if(status.getGeoLocation != null)
      rawTwitterObj.Xcoord = status.getGeoLocation.getLatitude.toString
    if(status.getGeoLocation != null)
      rawTwitterObj.Ycoord = status.getGeoLocation.getLongitude.toString

    if(status.getText != null)
      rawTwitterObj.text = status.getText
    if(status.getUser != null)
      rawTwitterObj.user = TwitterUser(status.getUser.getId.toString,status.getUser.getFollowersCount.toString,status.getUser.getFriendsCount.toString,
        status.getUser.getLang,status.getUser.getLocation,status.getUser.getScreenName)

    rawTwitterObj.retweeted =  compactRender(jsonMap \ "retweetCount" ).toString
    rawTwitterObj
  }
}
