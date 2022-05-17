package org.ssiu.ucp.spark.core.util

import com.beust.jcommander.JCommander
import org.slf4j.LoggerFactory
import org.ssiu.ucp.common.service.AppConfig
import org.ssiu.ucp.spark.core.command.SparkAppArgs
import org.ssiu.ucp.util.base.FileUtils

import java.nio.file.Paths

case class SparkAppConfigHolder private(appArgs: SparkAppArgs) {

  private final val LOG = LoggerFactory.getLogger(classOf[SparkAppConfigHolder])

  def getAppConfig: AppConfig = {
    appArgs.getDeployMode match {
      case "client" | "local" =>
        // use filePath
        val path = Paths.get(appArgs.getConfigFile).toAbsolutePath.toString
        AppConfig.fromPath(path)
      case "cluster" =>
        // use fileName
        val filePath = Paths.get(FileUtils.getFileName(appArgs.getConfigFile)).getFileName.toString
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
