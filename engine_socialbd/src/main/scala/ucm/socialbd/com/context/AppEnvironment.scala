package ucm.socialbd.com.context

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.TableEnvironment

object AppEnvironment {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  val tableEnv= TableEnvironment.getTableEnvironment(env)
}
