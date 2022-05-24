/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ssiu.ucp.spark.connector.fake

import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.catalyst.parser.CatalystSqlParser
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.types.{StructField, StructType}
import org.ssiu.ucp.spark.connector.fake.v2.FakeOption
import org.ssiu.ucp.spark.core.api.{SparkSingleBatchConnector, SparkSingleStreamConnector}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import org.ssiu.ucp.spark.core.util.ConfigImplicit._

import scala.collection.JavaConverters._

class Fake extends SparkSingleBatchConnector with SparkSingleStreamConnector {

  private final val FAKE_CONNECTOR_NAME = "ucp.fake"

  private final val FAKE_CONNECTOR_OUT = "console"

  /**
   * Read data from external storage
   *
   * @param env    runtime context
   * @param config element config
   * @return batch data
   */
  override def batchRead(env: SparkRuntimeEnv, config: Config): DataFrame = {
    env.sparkEnv.read
      .schema(buildSchema(config))
      .options(buildOptions(config))
      .format(FAKE_CONNECTOR_NAME)
      .load()
  }

  /**
   * write a single table to external storage in batch mode
   *
   * @param input  batch input
   * @param env    spark env
   * @param config element config
   */
  override protected def singleBatchWrite(input: DataFrame, env: SparkRuntimeEnv, config: Config): Unit = {
    input.show(false)
  }

  /**
   * Read data from external storage
   *
   * @param env    runtime context
   * @param config element config
   * @return stream table
   */
  override def streamRead(env: SparkRuntimeEnv, config: Config): DataFrame = {
    env.sparkEnv.readStream
      .schema(buildSchema(config))
      .format(FAKE_CONNECTOR_NAME)
      .load()
  }

  /**
   * write a single table to external storage in streaming mode
   *
   * @param input  stream input
   * @param env    spark env
   * @param config element config
   */
  override protected def singleStreamWrite(input: DataFrame, env: SparkRuntimeEnv, config: Config): StreamingQuery = {
    val sinkMode = config.optionalString(Fake.SINK_MODE).getOrElse(Fake.SINK_MODE_ONCE)

    val streamWriter = input.writeStream.format(FAKE_CONNECTOR_OUT)
    sinkMode match {
      case Fake.SINK_MODE_MICRO_BATCH => // use default config
      case Fake.SINK_MODE_CONTINUOUS =>
        val interval = config.optionalLong(Fake.CONTINUOUS_TIME_INTERVAL_MS).getOrElse(Fake.CONTINUOUS_TIME_INTERVAL_MS_DEFAULT)
        val trigger = Trigger.Continuous(interval)
        streamWriter.trigger(trigger)
      case Fake.SINK_MODE_ONCE =>
        streamWriter.trigger(Trigger.Once)
      case _ => throw new IllegalArgumentException(s"Unknown sink mode: $sinkMode")
    }

    streamWriter.start()
  }

  private def buildSchema(config: Config): StructType = {
    val structFields = config.optionalConfig(Fake.SCHEMA)
      .map(schema => {
        schema.root().asScala.map(kv => kv._1 -> kv._2.unwrapped().toString).toMap
      })
      .getOrElse(Fake.SCHEMA_DEFAULT)
      .map(kv => StructField(kv._1, CatalystSqlParser.parseDataType(kv._2)))
      .toSeq
    StructType(structFields)
  }

  private def buildOptions(config: Config): Map[String, String] = {
    val speed = config.optionalInt(FakeOption.SPEED).getOrElse(FakeOption.SPEED_DEFAULT)
    val numberMax = config.optionalInt(FakeOption.NUMBER_MAX).getOrElse(FakeOption.STR_MAX_LEN_DEFAULT)
    val strMaxLen = config.optionalInt(FakeOption.STR_MAX_LEN).getOrElse(FakeOption.STR_MAX_LEN_DEFAULT)
    Map[String, String](
      FakeOption.SPEED -> speed.toString,
      FakeOption.NUMBER_MAX -> numberMax.toString,
      FakeOption.STR_MAX_LEN -> strMaxLen.toString
    )
  }
}

/**
 * Other config:  [[FakeOption]]
 */
object Fake {
  private final val SCHEMA = "schema"
  private final val SCHEMA_DEFAULT = Map[String, String]("id" -> "int", "name" -> "string")

  // provide microBatch/once/continuous
  private final val SINK_MODE = "sinkMode"
  private final val SINK_MODE_MICRO_BATCH = "microBatch"
  private final val SINK_MODE_ONCE = "once" // default
  private final val SINK_MODE_CONTINUOUS = "continuous"
  private final val CONTINUOUS_TIME_INTERVAL_MS = "continuousTimeIntervalMs"
  private final val CONTINUOUS_TIME_INTERVAL_MS_DEFAULT = 1000L

}