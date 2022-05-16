package org.ssiu.ucp.spark.connector.fake.v2

import java.util

import org.apache.spark.sql.connector.catalog.{SupportsRead, TableCapability}
import org.apache.spark.sql.connector.read.ScanBuilder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import scala.collection.JavaConverters._

/**
 * A FakeTable that can generate data based on schema
 */
class FakeTable(userSchema: StructType) extends SupportsRead {

  override def name(): String = FakeTable.NAME

  override def schema(): StructType = userSchema

  /**
   * Support BATCH_READ now
   *
   * StreamRead coming soon
   */
  override def capabilities(): util.Set[TableCapability] = {
    (TableCapability.BATCH_READ :: Nil).toSet.asJava
  }

  override def newScanBuilder(caseInsensitiveStringMap: CaseInsensitiveStringMap): ScanBuilder = {
    new FakeTableScanBuilder(userSchema, caseInsensitiveStringMap)
  }
}

object FakeTable {
  final val NAME = "ucp.fake"
}
