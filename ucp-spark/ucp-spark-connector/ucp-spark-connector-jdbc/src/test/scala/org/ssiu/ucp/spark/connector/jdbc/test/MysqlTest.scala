package org.ssiu.ucp.spark.connector.jdbc.test

import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.junit.jupiter.api.{Assertions, BeforeEach, Test}
import org.ssiu.ucp.common.mode.ElementType
import org.ssiu.ucp.common.service.AppConfig
import org.ssiu.ucp.core.api.{BatchReader, BatchWriter, Plugin}
import org.ssiu.ucp.core.config.BasicConfig
import org.ssiu.ucp.core.service.PluginManager
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv

import scala.collection.JavaConverters._

// use test.conf to do test.
// with only one reader and one writer
//@unchecked
@Test
class MysqlTest {

  var jdbcPlugin: Plugin[SparkRuntimeEnv] = _

  var readerConfig: Config = _

  var writerConfig: Config = _

  var env: SparkRuntimeEnv = _

  @BeforeEach
  def setUp(): Unit = {
    val builder = AppConfig.fromName("test")
    val elements = builder.getElements.asScala

    // config
    readerConfig = elements.filter(e => e.getConfig.getEnum(classOf[ElementType], BasicConfig.ELEMENT_TYPE).equals(ElementType.Reader)).head.getConfig
    writerConfig = elements.filter(e => e.getConfig.getEnum(classOf[ElementType], BasicConfig.ELEMENT_TYPE).equals(ElementType.Writer)).head.getConfig

    // env
    env = SparkRuntimeEnv(builder.getJobConfig)
    env.prepare()

    // plugin
    val pluginM = new PluginManager[SparkRuntimeEnv]()
    pluginM.init()
    jdbcPlugin = pluginM.getAllPlugins.asScala
      .filter(_._1.equalsIgnoreCase("jdbc"))
      .head._2
  }


  @Test
  def testBatchRead(): Unit = {
    val bool1 = jdbcPlugin.isInstanceOf[BatchReader[SparkRuntimeEnv, DataFrame]]
    Assertions.assertTrue(bool1)
    val reader = jdbcPlugin.asInstanceOf[BatchReader[SparkRuntimeEnv, DataFrame]]
    reader.batchRead(env, readerConfig).show(false)
  }

  @Test
  def testBatchWrite(): Unit = {

    val bool1 = jdbcPlugin.isInstanceOf[BatchWriter[SparkRuntimeEnv, DataFrame]]
    Assertions.assertTrue(bool1)

    val writer = jdbcPlugin.asInstanceOf[BatchWriter[SparkRuntimeEnv, DataFrame]]
    val spark = env.sparkEnv
    import spark.implicits._
    val frame = spark.sparkContext.parallelize(
      (11, 11, "new") :: Nil
    ).toDF("customer_id", "amount", "account_name")
    val inputs = new java.util.HashMap[String, DataFrame]()
    inputs.put("test_in", frame)
    writer.batchWrite(inputs, env, writerConfig)
  }

  @Test
  def validateTest(): Unit = {
    val readCheck = jdbcPlugin.validateConf(readerConfig)
    val writeCheck = jdbcPlugin.validateConf(writerConfig)
    Assertions.assertTrue(!readCheck.asScala.exists(_.isErr))
    Assertions.assertTrue(!writeCheck.asScala.exists(_.isErr))

  }

}
