package org.ssiu.ucp.spark.operator.sql

import java.util

import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.util.CheckResult
import org.ssiu.ucp.spark.core.api.{SparkBatchOperator, SparkStreamOperator}
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv

class Sql extends SparkBatchOperator with SparkStreamOperator {


  /**
   * validate config
   *
   * @return a mutable list contains all check result
   */
  override def validateConf(config: Config): util.List[CheckResult] = {
    val results = super.validateConf(config)
    if (!config.hasPath(Sql.SQL)) {
      results.add(CheckResult.error(s"missing config in Sql operator in 'config' level: ${Sql.SQL}"))
    }
    // check sql syntax  may not match Spark's requirements. ignore
    results
  }

  /**
   * Query/process a batch data
   *
   * @param inputs tables. key is tableName in engine
   * @param env    runtime context
   * @param config element config
   * @return query result
   */
  override def batchQuery(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): DataFrame = {
    val code = config.getString(Sql.SQL)
    env.sparkEnv.sql(code)
  }

  /**
   * Query/process a stream data
   *
   * @param inputs tables. key is tableName in engine
   * @param env    runtime context
   * @param config element config
   * @return query result
   */
  override def streamQuery(inputs: util.Map[String, DataFrame], env: SparkRuntimeEnv, config: Config): DataFrame = {
    batchQuery(inputs, env, config)
  }
}

object Sql {
  val SQL = "sql"
}