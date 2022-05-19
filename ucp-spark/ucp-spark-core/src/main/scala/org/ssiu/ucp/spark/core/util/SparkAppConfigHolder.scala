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

package org.ssiu.ucp.spark.core.util

import java.nio.file.Paths

import com.beust.jcommander.JCommander
import org.slf4j.LoggerFactory
import org.ssiu.ucp.common.service.AppConfig
import org.ssiu.ucp.spark.core.command.SparkAppArgs
import org.ssiu.ucp.util.base.FileUtils

case class SparkAppConfigHolder private(appArgs: SparkAppArgs) {

  private final val LOG = LoggerFactory.getLogger(classOf[SparkAppConfigHolder])

  def getAppConfig: AppConfig = {
    LOG.info(s"Load app config in deploy-mode: ${appArgs.getDeployMode}")
    appArgs.getDeployMode match {
      case "client" | "local" =>
        // use filePath
        val path = Paths.get(appArgs.getConfigFile).toAbsolutePath.toString
        LOG.info(s"Load app config using filePath: $path")
        AppConfig.fromPath(path)
      case "cluster" =>
        // use fileName
        val filePath = Paths.get(FileUtils.getFileName(appArgs.getConfigFile)).getFileName.toString
        LOG.info(s"Load app config using fileName: $filePath")
        AppConfig.fromPath(filePath)
      case _ =>
        LOG.error(s"This deploy mode [${appArgs.getDeployMode}] is not supported for the time being, and is temporarily supported by: [client, local, cluster]")
        throw new IllegalArgumentException("For Spark, the unknown deploy mode")
    }
  }

}

object SparkAppConfigHolder {
  def apply(args: Array[String]): SparkAppConfigHolder = {
    val appArgs = new SparkAppArgs
    JCommander.newBuilder()
      .addObject(appArgs)
      .args(args)
      .build()
    SparkAppConfigHolder(appArgs)
  }
}
