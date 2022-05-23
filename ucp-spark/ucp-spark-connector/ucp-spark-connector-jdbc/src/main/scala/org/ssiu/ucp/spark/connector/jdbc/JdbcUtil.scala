package org.ssiu.ucp.spark.connector.jdbc

import java.sql.{Connection, PreparedStatement}

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.parser.CatalystSqlParser
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.unsafe.types.UTF8String

import scala.collection.mutable._

object JdbcUtil {

  def createInsertQuery(schema: StructType, jdbcConfig: JdbcConfig): String = {
    val columns = schema.map(sf => {
      sf.name
    })
    val table = jdbcConfig.tbName
    s"""INSERT INTO $table (${columns.mkString(",")})
       |VALUES (${columns.map(_ => "?").mkString(",")})
       |""".stripMargin
  }

  def cacheRow(pstm: PreparedStatement, schema: StructType, internalRow: InternalRow): Unit = {
    val seq = internalRow.toSeq(schema)
    seq.indices.foreach(idx => {
      val value = seq(idx)
      val pstmIdx = idx + 1
      // TODO: diff types
      value match {
        case str: UTF8String => pstm.setString(pstmIdx, str.toString)
        case integer: Int => pstm.setInt(pstmIdx, integer)
        case _ => pstm.setObject(pstmIdx, value)
      }
    })
    pstm.addBatch()
  }

  def getSchema(jdbcConfig: JdbcConfig): StructType = {
    var conn: Connection = null
    val list = ListBuffer[StructField]()
    try {
      conn = jdbcConfig.getConnection
      val columns = conn.getMetaData.getColumns(null, null, jdbcConfig.tbName, null)

      while (columns.next()) {
        val columnName = columns.getString("COLUMN_NAME");
        val datatype = columns.getString("DATA_TYPE")
        val isNullable = columns.getString("IS_NULLABLE")
        list += StructField(columnName, CatalystSqlParser.parseDataType(datatype), nullable = true)
      }
    } finally {
      conn.close()
    }
    StructType(list)
  }

}
