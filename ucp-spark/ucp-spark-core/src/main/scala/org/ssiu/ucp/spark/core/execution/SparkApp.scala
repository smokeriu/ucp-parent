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

package org.ssiu.ucp.spark.core.execution

import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.common.mode.JobLevel
import org.ssiu.ucp.common.service.AppConfig
import org.ssiu.ucp.core.execution.AppTrait
import org.ssiu.ucp.core.service.TableProvider
import org.ssiu.ucp.core.util.CheckResult
import org.ssiu.ucp.core.workflow.{AbstractFlow, UniversalFlow}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import org.ssiu.ucp.spark.core.service.SparkTableProvider

import java.{util => ju}

class SparkApp private(val appConfig: AppConfig) extends AppTrait {

  private var env: SparkRuntimeEnv = _

  private var tableProvider: TableProvider[DataFrame] = _

  private var workFlow: AbstractFlow = _

  /**
   * prepare work for app
   */
  override def prepareApp(): Unit = {
    env = SparkRuntimeEnv(appConfig.getJobConfig)
    env.prepare()
    tableProvider = SparkTableProvider()
    workFlow = new UniversalFlow[SparkRuntimeEnv, DataFrame](appConfig.getElements, tableProvider, env)
    workFlow.initFlow()
  }

  /**
   * Check app is validate
   */
  override def checkApp(): ju.List[CheckResult] = {
    val envRes = env.checkConfig()
    val results = workFlow.validateFlow()
    results.addAll(envRes)
    results
  }

  /**
   * submit app
   */
  override def submit(): Unit = {
    workFlow.runFlow()
  }

  /**
   * @return is dev app or release app
   */
  override def appLevel(): JobLevel = env.jobConfig.getJobLevel
}

object SparkApp {
  def apply(appConfig: AppConfig): SparkApp = new SparkApp(appConfig)
}