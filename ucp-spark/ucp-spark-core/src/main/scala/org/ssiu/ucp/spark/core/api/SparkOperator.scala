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

package org.ssiu.ucp.spark.core.api

import java.util
import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.api.{BatchOperator, StreamOperator}
import org.ssiu.ucp.core.config.OperatorConfig
import org.ssiu.ucp.core.util.CheckResult
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import java.{util => ju}
// Common implementation combinations


/**
 * A operator support batch query
 */
trait SparkBatchOperator extends BatchOperator[SparkRuntimeEnv, DataFrame, DataFrame]

/**
 * A operator support stream query
 */
trait SparkStreamOperator extends StreamOperator[SparkRuntimeEnv, DataFrame, DataFrame]

/**
 * A one to one batch operator
 */
trait SparkSingleInputBatchOperator extends SparkBatchOperator {

  import scala.collection.JavaConverters._

  override def batchQuery(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): DataFrame = {
    singleInputBatchQuery(inputs.asScala.head._2, env, config)
  }

  /**
   * Query a single input batch table.
   *
   * @param input  batch table
   * @param env    spark env
   * @param config element config
   * @return query result. is also a batch table
   */
  protected def singleInputBatchQuery(input: DataFrame, env: SparkRuntimeEnv, config: Config): DataFrame
}

/**
 * A one to one stream operator
 */
trait SparkSingleInputStreamOperator extends SparkStreamOperator {

  import scala.collection.JavaConverters._

  override def streamQuery(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): DataFrame = {
    singleInputStreamQuery(inputs.asScala.head._2, env, config)
  }

  /**
   * Query a single input stream table.
   *
   * @param input  stream table
   * @param env    spark env
   * @param config element config
   * @return query result. is also a stream table
   */
  protected def singleInputStreamQuery(input: DataFrame, env: SparkRuntimeEnv, config: Config): DataFrame

}

/**
 * A two to one batch operator
 *
 * @note class impl this trait should provide config [[OperatorConfig]].
 */
trait SparkTwoInputBatchOperator extends SparkBatchOperator {

  import scala.collection.JavaConverters._

  /**
   * validate config
   */
  override def validateConf(config: Config): ju.List[CheckResult] = {
    if (config.hasPath(OperatorConfig.LEFT) && config.hasPath(OperatorConfig.RIGHT)) {
      CheckResult.success()
    }
    CheckResult.singleErrorList("config should has left and right fields")
  }

  override def batchQuery(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): DataFrame = {
    val twoEle = inputs.asScala
    twoInputBatchQuery(twoEle(OperatorConfig.LEFT), twoEle(OperatorConfig.RIGHT), env, config)
  }

  /**
   * Query a batch table with two inputs
   *
   * @param left   left batch table
   * @param right  right batch table
   * @param env    spark env
   * @param config element config
   * @return query result. is also a batch table
   */
  protected def twoInputBatchQuery(left: DataFrame, right: DataFrame, env: SparkRuntimeEnv, config: Config): DataFrame
}

/**
 * A two to one stream operator
 *
 * @note class impl this trait should provide config [[OperatorConfig]].
 */
trait SparkTwoInputStreamOperator extends SparkStreamOperator {

  import scala.collection.JavaConverters._

  /**
   * validate config
   */
  override def validateConf(config: Config): ju.List[CheckResult] = {
    if (config.hasPath(OperatorConfig.LEFT) && config.hasPath(OperatorConfig.RIGHT)) {
      CheckResult.success()
    }
    CheckResult.singleErrorList("config should has left and right fields")
  }

  override def streamQuery(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): DataFrame = {
    val twoEle = inputs.asScala
    twoInputStreamQuery(twoEle(OperatorConfig.LEFT), twoEle(OperatorConfig.RIGHT), env, config)
  }

  /**
   * Query a stream table with two inputs
   *
   * @param left   left stream table
   * @param right  right stream table
   * @param env    spark env
   * @param config element config
   * @return query result. is also a batch table
   */
  protected def twoInputStreamQuery(left: DataFrame, right: DataFrame, env: SparkRuntimeEnv, config: Config): DataFrame
}