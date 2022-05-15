package org.ssiu.ucp.spark.core.api

import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.api.UcpTable

case class SparkUcpTable() extends UcpTable[DataFrame]

object SparkUcpTable {
  def apply(tableName: String, holdTable: DataFrame): SparkUcpTable = {
    val table = new SparkUcpTable
    table.setHoldTable(holdTable)
    table.setName(tableName)
    table
  }
}