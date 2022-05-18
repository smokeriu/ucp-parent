package org.ssiu.ucp.spark.operator.code

import java.nio.file.{Files, Paths}
import java.util

import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expression.CodeInvokeColumn
import org.apache.spark.sql.functions.col
import org.ssiu.ucp.core.util.{CheckResult, CheckResultUtil}
import org.ssiu.ucp.spark.core.api.{SparkSingleInputBatchOperator, SparkSingleInputStreamOperator}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv
import org.ssiu.ucp.spark.core.util.SparkConfig
import org.ssiu.ucp.spark.operator.code.Code.readCodeFromFile
import org.ssiu.ucp.util.base.FileUtils

import scala.collection.JavaConverters._

class Code extends SparkSingleInputBatchOperator with SparkSingleInputStreamOperator {


  /**
   * validate config
   *
   * @return a mutable list contains all check result
   */
  override def validateConf(config: Config): util.List[CheckResult] = {
    val results = super.validateConf(config)

    val thisResults = CheckResultUtil.checkAllExists(config, "Code operator",
      Code.CODE_FILE, Code.METHOD_NAME, Code.RETURN_NAME, Code.RETURN_TYPE)
    results.addAll(thisResults)

    results
  }

  /**
   * Query a single input batch table.
   *
   * @param input  batch table
   * @param env    spark env
   * @param config element config
   * @return query result. is also a batch table
   */
  override protected def singleInputBatchQuery(input: DataFrame, env: SparkRuntimeEnv, config: Config): DataFrame = {
    val code = readCodeFromFile(env, config.getString(Code.CODE_FILE))
    val methodName = config.getString(Code.METHOD_NAME)
    val returnName = config.getString(Code.RETURN_NAME)
    val returnType = config.getString(Code.RETURN_TYPE)

    // Clean up inputs to suit code
    val (in, args) = Code.buildConvertExpression(input, config)

    // invoke code
    in.withColumn(returnName, CodeInvokeColumn(code, methodName, returnType, args.map(col): _*))
  }

  /**
   * Query a single input stream table.
   *
   * @param input  stream table
   * @param env    spark env
   * @param config element config
   * @return query result. is also a stream table
   */
  override protected def singleInputStreamQuery(input: DataFrame, env: SparkRuntimeEnv, config: Config): DataFrame = singleInputBatchQuery(input, env, config)
}

/**
 * provide config key & defaults value
 */
object Code {
  // Required
  private final val CODE_FILE = "codeFile"
  private final val METHOD_NAME = "methodName"
  private final val RETURN_NAME = "returnName"
  private final val RETURN_TYPE = "returnType"

  /**
   * Specify input columns orders list/map.
   *
   * If is List. We assume that the type required by the code is the same as the actual type of the data
   *
   * If is Map. We need to provide type conversions.
   */
  private final val INPUT_COLUMNS = "inputColumns"
  private final val SPLIT_CHAR = ":"


  private def readCodeFromFile(env: SparkRuntimeEnv, codeFilePath: String) = {
    val deployMode = env.sparkEnv.conf.get(SparkConfig.SPARK_SUBMIT_DEPLOY_MODE).toLowerCase()
    val path = deployMode match {
      case "client" => Paths.get(codeFilePath).toAbsolutePath
      case "cluster" => Paths.get(FileUtils.getFileName(codeFilePath)).getFileName
      case _ => throw new IllegalArgumentException("Unsupported deploy mode: " + deployMode)
    }
    // java 8 version
    Files.readAllLines(path).asScala.mkString
  }

  private def buildConvertExpression(input: DataFrame, config: Config) = {
    if (config.hasPath(INPUT_COLUMNS)) {
      val args = config.getList(Code.INPUT_COLUMNS).asScala.map(_.unwrapped().toString).toList
      args.head.split(SPLIT_CHAR).length match {
        case 1 =>
          // Just column name
          input -> args
        case 2 =>
          // name,type
          val nameType = args.map(arg => {
            val splits = arg.split(SPLIT_CHAR)
            splits(0) -> splits(1)
          })
          // Remove irrelevant column names
          val changelessColumns = input.columns.diff(nameType.map(_._1)).map(col)
          val convertColumns = nameType.map(kv => col(kv._1).cast(kv._2).as(kv._1)
          )
          input.select(changelessColumns ++ convertColumns: _*) -> args.map(_.split(SPLIT_CHAR).head)
      }
    } else {
      input -> List.empty[String]
    }
  }

}