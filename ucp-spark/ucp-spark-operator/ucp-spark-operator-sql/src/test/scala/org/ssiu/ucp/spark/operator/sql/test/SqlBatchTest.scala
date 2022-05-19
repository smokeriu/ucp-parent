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

package org.ssiu.ucp.spark.operator.sql.test

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.DataFrame
import org.junit.jupiter.api.{BeforeEach, Test}
import org.ssiu.ucp.common.config.JobConfig
import org.ssiu.ucp.common.mode.JobMode
import org.ssiu.ucp.core.api.{BatchOperator, StreamOperator}
import org.ssiu.ucp.core.service.{PluginManager, TableProvider}
import org.ssiu.ucp.spark.core.api.{SparkBatchOperator, SparkStreamOperator}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import org.ssiu.ucp.spark.core.service.SparkTableProvider

@Test
class SqlBatchTest {

  var pm: PluginManager[SparkRuntimeEnv] = _

  var tableProvider: TableProvider[DataFrame] = _

  var batchEnv: SparkRuntimeEnv = _

  @BeforeEach
  def setUp(): Unit ={
    pm = new PluginManager[SparkRuntimeEnv]
    pm.init()
    tableProvider = SparkTableProvider()
    val batchConfig = new JobConfig
    batchConfig.setJobMode(JobMode.Batch)
    batchEnv = SparkRuntimeEnv(batchConfig)
    batchEnv.prepare()
  }

  @Test
  def validateType(): Unit ={
    val sqlPlugin = pm.getPluginByName("Sql")
    classOf[SparkBatchOperator].isAssignableFrom(sqlPlugin.getClass)
  }

  @Test
  def test(): Unit ={
    val spark = batchEnv.sparkEnv
    import spark.implicits._
    val middleName = "tb"
    val frame = spark.sparkContext.parallelize(1 :: 2 :: 3 :: Nil).toDF("id")
    tableProvider.addTable(middleName,frame)
    val config = ConfigFactory.parseString("sql: \"select id from tb\"")
    val sqlPlugin = pm.getPluginByName("Sql").asInstanceOf[SparkBatchOperator]
    val out = sqlPlugin.batchQuery(null, batchEnv, config)
    out.show(false)
  }



}
