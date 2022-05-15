package org.ssiu.ucp.spark.operator.sql.test

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.DataFrame
import org.junit.jupiter.api.{BeforeEach, Test}
import org.ssiu.ucp.common.config.JobConfig
import org.ssiu.ucp.common.mode.JobMode
import org.ssiu.ucp.core.service.{PluginManager, TableProvider}
import org.ssiu.ucp.spark.core.api.{SparkBatchOperator, SparkStreamOperator}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import org.ssiu.ucp.spark.core.service.SparkTableProvider

@Test
class SqlStreamTest {

  var pm: PluginManager[SparkRuntimeEnv] = _

  var tableProvider: TableProvider[DataFrame] = _

  var streamEnv: SparkRuntimeEnv = _

  @BeforeEach
  def setUp(): Unit ={
    pm = new PluginManager[SparkRuntimeEnv]
    pm.init()
    tableProvider = SparkTableProvider()
    val batchConfig = new JobConfig
    batchConfig.setJobMode(JobMode.StructStreaming)
    streamEnv = SparkRuntimeEnv(batchConfig)
    streamEnv.prepare()
  }

  @Test
  def validateType(): Unit ={
    val sqlPlugin = pm.getPluginByName("Sql")
    classOf[SparkStreamOperator].isAssignableFrom(sqlPlugin.getClass)
  }

  @Test
  def test(): Unit ={
    // validate this after fake source
  }
}
