package org.ssiu.ucp.spark.connector.jdbc

import java.util
import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, DataFrameReader}
import org.ssiu.ucp.core.util.CheckResult
import org.ssiu.ucp.spark.connector.jdbc.Jdbc.validateConfOption
import org.ssiu.ucp.spark.core.api.{SparkSingleBatchConnector, SparkSingleStreamWriter}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import org.ssiu.ucp.spark.core.util.ConfigImplicit.RichConfig
import org.ssiu.ucp.spark.core.util.SparkConfig

import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.mutable

class Jdbc extends SparkSingleBatchConnector with SparkSingleStreamWriter {


  /**
   * validate config
   *
   * @return a mutable list contains all check result
   */
  override def validateConf(config: Config): util.List[CheckResult] = {
    val rawResults = super.validateConf(config)
    val addResult: CheckResult => Boolean = rawResults.add
    val rootLevel = "'config'"
    val hasConnInfo = validateConfOption(config, Jdbc.CONNECT_INFO, addResult, rootLevel)
    if (hasConnInfo) {
      val connInfo = config.getConfig(Jdbc.CONNECT_INFO)
      validateConfOption(connInfo, Jdbc.USER, addResult, Jdbc.CONNECT_INFO)
      validateConfOption(connInfo, Jdbc.USER, addResult, Jdbc.CONNECT_INFO)
      validateConfOption(connInfo, Jdbc.USER, addResult, Jdbc.CONNECT_INFO)
      // Check if the url is configured
      val urlEnable = connInfo.optionalString(Jdbc.URL)
        .map(_ => true)
        // If not configured, check url Format, etc.
        .orElse {
          Some(connInfo.hasPath(Jdbc.FORMAT) && connInfo.hasPath(Jdbc.HOST) && connInfo.hasPath(Jdbc.PORT) && connInfo.hasPath(Jdbc.DB_NAME))
        }.get
      if (!urlEnable) {
        rawResults.add(CheckResult.error(s"missing config in Jdbc connector in connectInfo level: Either set ${Jdbc.URL}, or set ${Jdbc.FORMAT} & ${Jdbc.DB_NAME} & ${Jdbc.HOST} & ${Jdbc.PORT}"))
      }
    }

    // check if is increment or distribute
    config.optionalString(Jdbc.READ_MODE).foreach {
      case Jdbc.READ_MODE_DISTRIBUTE =>
        validateConfOption(config, Jdbc.DISTRIBUTE_COLUMN, addResult, rootLevel)
        validateConfOption(config, Jdbc.UPPER_LIMIT, addResult, rootLevel)
        validateConfOption(config, Jdbc.LOWER_LIMIT, addResult, rootLevel)
      case Jdbc.READ_MODE_INCREMENT =>
        validateConfOption(config, Jdbc.DISTRIBUTE_COLUMN, addResult, rootLevel)
        validateConfOption(config, Jdbc.START_TIME, addResult, rootLevel)
        validateConfOption(config, Jdbc.END_TIME, addResult, rootLevel)
      case Jdbc.READ_MODE_SINGLE =>
      case hasSet => rawResults.add(CheckResult.error(s"unknown Jdbc connector readMode: $hasSet"))
    }

    rawResults
  }


  /**
   * write a single table to external storage in batch mode
   *
   * @param input  batch input
   * @param env    spark env
   * @param config element config
   */
  override protected def singleBatchWrite(input: DataFrame, env: SparkRuntimeEnv, config: Config): Unit = {
    jdbcBatchWrite(input, config)
  }

  /**
   * Define how to read batch data from external storage
   *
   * @param config connector config
   * @return Retrieved data
   * @throws Exception exception
   */
  override def batchRead(env: SparkRuntimeEnv, config: Config): DataFrame = {
    val readMode = config.optionalString(Jdbc.READ_MODE).getOrElse(Jdbc.READ_MODE_DEFAULT)
    val cores = env.sparkEnv.conf.get(SparkConfig.EXECUTOR_CORES, "1").toInt
    val executors = env.sparkEnv.conf.get(SparkConfig.EXECUTOR_INSTANCES, "1").toInt
    val numPartitions = cores * executors
    val reader = env.sparkEnv.read.format("jdbc")
    readMode match {
      case Jdbc.READ_MODE_INCREMENT => incrementRead(reader, config, numPartitions)
      case Jdbc.READ_MODE_SINGLE => singleRead(reader, config)
      case Jdbc.READ_MODE_DISTRIBUTE => distributedRead(reader, config, numPartitions)
      case _ => throw new Exception(s"not support this readMode: $readMode")
    }
  }

  protected def incrementRead(reader: DataFrameReader, config: Config, numPartitions: Int): DataFrame = {
    val startTime = config.getLong(Jdbc.START_TIME)
    val endTime = config.getLong(Jdbc.END_TIME)
    val distributeColumn = config.getString(Jdbc.DISTRIBUTE_COLUMN)
    val predicates = incrementReadPredicates(config, startTime, endTime, numPartitions, distributeColumn)
    val sparkConfig = sparkOptions(config)
    reader
      .options(sparkConfig)
      .jdbc(sparkConfig(JDBCOptions.JDBC_URL), sparkConfig(JDBCOptions.JDBC_TABLE_NAME), predicates, new Properties())
  }

  protected def singleRead(reader: DataFrameReader, config: Config): DataFrame = {
    val sparkConfig = sparkOptions(config)
    reader.options(sparkConfig).load()
  }

  protected def distributedRead(reader: DataFrameReader, config: Config, numPartitions: Int): DataFrame = {
    val sparkConfig = sparkOptions(config)
    distributeOptions(config, sparkConfig, numPartitions)
    reader.options(sparkConfig).load()
  }


  /**
   * add distribute only config to jdbcConf from extra conf
   */
  protected def distributeOptions(config: Config, jdbcOptions: mutable.Map[String, String], numPartitions: Int): Unit = {
    jdbcOptions.put(JDBCOptions.JDBC_UPPER_BOUND, config.getString(Jdbc.UPPER_LIMIT))
    jdbcOptions.put(JDBCOptions.JDBC_LOWER_BOUND, config.getString(Jdbc.LOWER_LIMIT))
    jdbcOptions.put(JDBCOptions.JDBC_PARTITION_COLUMN, config.getString(Jdbc.DISTRIBUTE_COLUMN))
    jdbcOptions.put(JDBCOptions.JDBC_NUM_PARTITIONS, numPartitions.toString)
  }

  /**
   * sparkOptions use options method
   *
   * @param config common options for all mode
   * @return
   */
  protected def sparkOptions(config: Config): mutable.Map[String, String] = {
    val connectInfo = JdbcConfig(config.getConfig(Jdbc.CONNECT_INFO))
    val extraConfig = config.optionalConfig(Jdbc.EXTRA_OPTIONS).getOrElse(ConfigFactory.empty())
    jdbcOptions(connectInfo) ++ sparkExtraOptions(extraConfig)
  }

  /**
   * build common jdbcOption from extraConf
   */
  protected def jdbcOptions(connectInfo: JdbcConfig): mutable.Map[String, String] = {
    val props = new mutable.HashMap[String, String]()
    props.put(JDBCOptions.JDBC_DRIVER_CLASS, connectInfo.driver)
    props.put(Jdbc.USER, connectInfo.user)
    props.put(Jdbc.PASSWORD, connectInfo.password)
    props.put(JDBCOptions.JDBC_URL, connectInfo.url)
    props.put(JDBCOptions.JDBC_TABLE_NAME, connectInfo.tbName)
    props
  }

  /**
   * get extra sparkOptions from config
   *
   * @param extraConfig extra config root
   * @return
   */
  protected def sparkExtraOptions(extraConfig: Config): mutable.Map[String, String] = {
    extraConfig
      .root().asScala
      .map(kv => kv._1 -> kv._2.unwrapped().toString)
  }

  /**
   * build predicates to support increment read.
   *
   * Some databases need to override this method to meet the time format requirements
   */
  protected def incrementReadPredicates(config: Config,
                                        startTime: Long,
                                        endTime: Long,
                                        numPartitions: Int,
                                        incrementColumn: String): Array[String] = {
    // build predicates
    buildTimeTupleList(startTime, endTime, numPartitions)
      .map(tp2 => {
        s"$incrementColumn > ${tp2._1} and $incrementColumn <= ${tp2._2}"
      }).toArray
  }

  protected def buildTimeTupleList(startTime: Long, endTime: Long, numPartitions: Int): Seq[(Long, Long)] = {
    val stepLong = (endTime - startTime) / numPartitions
    val head = (1 until numPartitions).map(idx => {
      val realStart = startTime + stepLong * (idx - 1)
      val realEnd = startTime + stepLong * idx
      (realStart, realEnd)
    })

    // We need to make sure the last place is exactly the same as the end time
    val lastStart = head.maxBy(_._2)._2
    head.+:((lastStart, endTime))
  }

  protected def jdbcBatchWrite(input: DataFrame, config: Config): Unit = {
    val saveMode = config.optionalString(Jdbc.SAVE_MODE).getOrElse(Jdbc.SAVE_MODE_DEFAULT)
    val sparkConfig = sparkOptions(config)
    input.write
      .mode(saveMode)
      .format("jdbc")
      .options(sparkConfig)
      .save()
  }

  /**
   * write a single table to external storage in streaming mode
   *
   * @param input  stream input
   * @param env    spark env
   * @param config element config
   */
  override protected def singleStreamWrite(input: DataFrame, env: SparkRuntimeEnv, config: Config): Unit = {
    // Currently, we only offer Mode micro batch.
    val sparkConfig = sparkOptions(config)
    val trigger = Trigger.ProcessingTime(config.optionalLong(Jdbc.MICRO_BATCH_INTERVAL).getOrElse(Jdbc.MICRO_BATCH_INTERVAL_DEFAULT))
    val query = input.writeStream
      .format("ucp.jdbc")
      .options(sparkConfig)
      .trigger(trigger)
      .start()

    query.awaitTermination()
  }
}

object Jdbc {
  // connectInfo
  val CONNECT_INFO: String = "connectInfo"
  val URL: String = "url"
  val FORMAT: String = "format"
  val FORMAT_MYSQL: String = "jdbc:mysql://%s:%d/%s?%s"

  val DRIVER: String = "driver"

  val HOST: String = "host"

  val PORT: String = "port"

  val DB_NAME: String = "dbName"

  val TB_NAME: String = "tbName"

  val CONNECT_OPTIONS: String = "connectOptions"

  val USER: String = "user"

  val PASSWORD: String = "password"

  //// Read option
  // single(default), increment, distribute
  val READ_MODE: String = "readMode"
  val READ_MODE_SINGLE = "single"
  val READ_MODE_INCREMENT = "increment"
  val READ_MODE_DISTRIBUTE = "distribute"
  val READ_MODE_DEFAULT: String = READ_MODE_SINGLE


  // increment & distribute only
  val DISTRIBUTE_COLUMN: String = "distributeColumn"

  // increment only
  val START_TIME: String = "startTime"
  val END_TIME: String = "endTime"

  // distribute only
  val UPPER_LIMIT: String = "upper"
  val LOWER_LIMIT: String = "lower"

  // extra options
  val EXTRA_OPTIONS = "extraOptions"

  //// WriteOption
  // saveMode
  val SAVE_MODE: String = "saveMode"
  val SAVE_MODE_DEFAULT = "append"

  // stream use
  val MICRO_BATCH_INTERVAL = "microBatchInterval"
  val MICRO_BATCH_INTERVAL_DEFAULT = 1000L

  private def validateConfOption[T](config: Config,
                                    option: String,
                                    addResFunc: CheckResult => T,
                                    level: String): Boolean = {
    if (!config.hasPath(option)) {
      addResFunc(missingConfigCheckResult(option, level))
      false
    } else {
      true
    }
  }

  private def missingConfigCheckResult(missingConfig: String, level: String): CheckResult = {
    CheckResult.error(s"missing config in Jdbc connector in $level level: $missingConfig")
  }


}