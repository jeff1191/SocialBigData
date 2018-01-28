package ucm.socialbd.com.cluster

import ucm.socialbd.com.config.IngestSBDProperties

/**
  * Created by Jeff on 24/04/2017.
  */
trait EmbeddedCluster {

  def initCluster(socialBDProperties: IngestSBDProperties): Unit
}
