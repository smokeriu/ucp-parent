package org.ssiu.ucp.spark.core

import org.ssiu.ucp.core.execution.UcpWork
import org.ssiu.ucp.spark.core.execution.SparkApp
import org.ssiu.ucp.spark.core.util.SparkAppConfigHolder

/**
 * using to start spark app
 */
object App {

  def main(args: Array[String]): Unit = {
    val appArgs = SparkAppConfigHolder(args)
    val sparkApp = SparkApp(appArgs.getAppConfig)
    UcpWork.execute(sparkApp)
  }

}
