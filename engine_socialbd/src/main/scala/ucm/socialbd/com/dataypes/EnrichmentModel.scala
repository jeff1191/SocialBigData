package ucm.socialbd.com.dataypes

import ucm.socialbd.com.dataypes.RawModel.TwitterUser

/**
  * Created by Jeff on 15/04/2017.
  */
object EnrichmentModel {
  case class EAir(estacion: String,
                  codigoEst: String,
                  Xcoord: String,
                  Ycoord: String,
                  fechaHora: String,
                  magnitudNombre: String,
                  magnitudCod: String,
                  tecnicaNom: String,
                  tecnicaCod: String,
                  valor: String,
                  nivelIntensidadTrafico: Double) extends EnrichmentObj
  case class ETraffic(idelem : String,
                      identif : String,
                      nombre: String,
                      fechaHora: String,
                      tipo_elem : String,
                      intensidad : String,
                      carga : String,
                      vmed : String,
                      error : String,
                      Xcoord : String,
                      Ycoord : String)extends EnrichmentObj

  case class ETweet(var id_str:String,
                    var createdAt:String,
                    var Xcoord:String,
                    var Ycoord:String,
                    var place:String,
                    var text:String,
                    var user:TwitterUser,
                    var retweeted:String )extends EnrichmentObj
}
