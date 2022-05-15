package org.ssiu.ucp.spark.core.env

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}
import org.ssiu.ucp.common.config.JobConfig
import org.ssiu.ucp.common.mode.JobMode
import org.ssiu.ucp.core.env.RuntimeEnv
import org.ssiu.ucp.core.util.CheckResult
import org.ssiu.ucp.spark.core.util.SparkConfig

import java.util

case class SparkRuntimeEnv(var jobConfig: JobConfig) extends RuntimeEnv {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[SparkRuntimeEnv])

  private val DEFAULT_SPARK_STREAMING_DURATION = 5

  private var _sparkContext: SparkContext = _

  def sparkContext: SparkContext = _sparkContext

  private var _sparkEnv: SparkSession = _

  def sparkEnv: SparkSession = _sparkEnv

  private var streamEnv: StreamingContext = _

  override def setConfig(config: JobConfig): Unit = {
    this.jobConfig = config
  }

  override def getConfig: JobConfig = {
    this.jobConfig
  }

  override def checkConfig(): util.List[CheckResult] = {
    CheckResult.singleSuccessList()
  }


  override def prepare(): Unit = {
    val sparkConf = createSparkConf()
    val builder = SparkSession.builder().config(sparkConf)
    _sparkEnv = builder.getOrCreate()
    _sparkContext = _sparkEnv.sparkContext
    if (jobConfig.getJobMode == JobMode.Streaming) {
      createStreamingContext()
    }
  }

  private def createStreamingContext(): Unit = {
    val duration = sparkContext.getConf.getLong("spark.stream.batchDuration", DEFAULT_SPARK_STREAMING_DURATION)
    streamEnv = new StreamingContext(sparkContext, Seconds.apply(duration))
  }

  private def createSparkConf() = {
    val sparkConf = new SparkConf()
    sparkConf.set(SparkConfig.SPARK_MASTER, sparkConf.get(SparkConfig.SPARK_MASTER, SparkConfig.SPARK_MASTER_LOCAL))
    sparkConf
  }

  override def isStreaming: Boolean = {
    jobConfig.getJobMode match {
      case JobMode.Batch => false
      case JobMode.Streaming => true
      case JobMode.StructStreaming => true
    }
  }


}
