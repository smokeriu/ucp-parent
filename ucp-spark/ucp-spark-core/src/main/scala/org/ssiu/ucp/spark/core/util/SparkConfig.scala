package org.ssiu.ucp.spark.core.util

//TODO: find the follow configs key in spark.jar
object SparkConfig {

  final val EXECUTOR_INSTANCES = "spark.executor.instances"

  final val EXECUTOR_CORES = "spark.executor.cores"

  final val EXECUTOR_MEMORY = "spark.executor.memory"

  final val DRIVER_MEMORY = "spark.driver.memory"

  final val DRIVER_CORES = "spark.driver.cores"

  final val SPARK_MASTER = "spark.master"
  final val SPARK_MASTER_LOCAL = "local[4]"

}
