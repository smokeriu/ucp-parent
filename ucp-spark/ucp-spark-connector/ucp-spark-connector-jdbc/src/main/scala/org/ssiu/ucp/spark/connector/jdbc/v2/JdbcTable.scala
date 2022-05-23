package org.ssiu.ucp.spark.connector.jdbc.v2

import java.util

import org.apache.spark.sql.connector.catalog.{SupportsWrite, TableCapability}
import org.apache.spark.sql.connector.write.{LogicalWriteInfo, WriteBuilder}
import org.apache.spark.sql.types.StructType

import scala.collection.JavaConverters.setAsJavaSetConverter

class JdbcTable(writeSchema: StructType) extends SupportsWrite {
  override def newWriteBuilder(info: LogicalWriteInfo): WriteBuilder = {
    new JdbcWriteBuilder(info)
  }

  override def name(): String = JDBC.SHORT_NAME

  override def schema(): StructType = writeSchema

  override def capabilities(): util.Set[TableCapability] = (TableCapability.STREAMING_WRITE :: Nil).toSet.asJava
}
