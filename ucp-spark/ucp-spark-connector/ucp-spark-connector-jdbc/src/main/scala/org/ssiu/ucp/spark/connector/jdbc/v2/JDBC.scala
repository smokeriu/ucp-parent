package org.ssiu.ucp.spark.connector.jdbc.v2

import java.util

import org.apache.spark.sql.connector.catalog.{Table, TableProvider}
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.ssiu.ucp.spark.connector.jdbc.{JdbcConfig, JdbcUtil}

class JDBC extends TableProvider {
  override def inferSchema(options: CaseInsensitiveStringMap): StructType = {
    val config = JdbcConfig(options)
    JdbcUtil.getSchema(config)
  }

  override def getTable(schema: StructType, partitioning: Array[Transform], properties: util.Map[String, String]): Table = {
    new JdbcTable(schema)
  }

  override def supportsExternalMetadata(): Boolean = true
}

object JDBC {
  final val SHORT_NAME = "ucp-jdbc"
}
