package org.ssiu.ucp.spark.core.api

import java.util

import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.api.{BatchReader, BatchWriter, StreamReader, StreamWriter}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv

// Common implementation combinations


trait SparkBatchReader extends BatchReader[SparkRuntimeEnv, DataFrame]

trait SparkBatchWriter extends BatchWriter[SparkRuntimeEnv, DataFrame]

trait SparkStreamReader extends StreamReader[SparkRuntimeEnv, DataFrame]

trait SparkStreamWriter extends StreamWriter[SparkRuntimeEnv, DataFrame]

/**
 * A connector has stream write support.
 *
 * But only one table can be writer
 */
trait SparkSingleStreamWriter extends SparkStreamWriter {

  import scala.collection.JavaConverters._

  override def streamWrite(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): Unit = {
    singleStreamWrite(inputs.asScala.head._2, env, config)
  }

  /**
   * write a single table to external storage in streaming mode
   *
   * @param input  stream input
   * @param env    spark env
   * @param config element config
   */
  protected def singleStreamWrite(input: DataFrame, env: SparkRuntimeEnv, config: Config): Unit
}

/**
 * A connector has batch write support.
 *
 * But only one table can be writer
 */
trait SparkSingleBatchWriter extends SparkBatchWriter {

  import scala.collection.JavaConverters._

  override def batchWrite(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): Unit = {
    singleBatchWrite(inputs.asScala.head._2, env, config)
  }

  /**
   * write a single table to external storage in batch mode
   *
   * @param input  batch input
   * @param env    spark env
   * @param config element config
   */
  protected def singleBatchWrite(input: DataFrame, env: SparkRuntimeEnv, config: Config): Unit
}

/**
 * A connector support batch
 */
trait SparkSingleBatchConnector extends SparkBatchReader with SparkSingleBatchWriter

/**
 * A connector support stream
 */
trait SparkSingleStreamConnector extends SparkStreamReader with SparkSingleStreamWriter

