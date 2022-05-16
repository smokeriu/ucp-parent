package org.ssiu.ucp.spark.connector.fake.v2

import org.apache.spark.sql.connector.read.streaming.MicroBatchStream
import org.apache.spark.sql.connector.read.{Batch, Scan, ScanBuilder}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

/**
 * To create [[FakeTableScan]] by user specify schema & options
 *
 * @param schema user specify schema
 * @param option use specify options
 */
class FakeTableScanBuilder(schema: StructType, option: CaseInsensitiveStringMap) extends ScanBuilder {
  override def build(): Scan = {
    new FakeTableScan(schema, option)
  }
}

/**
 * Support [[FakeBatch]] & etc..
 */
class FakeTableScan(schema: StructType, option: CaseInsensitiveStringMap)
  extends Scan {

  override def readSchema(): StructType = schema

  override def toBatch: Batch = {
    new FakeBatch(schema, FakeOption(option))
  }

  override def toMicroBatchStream(checkpointLocation: String): MicroBatchStream = {
    new FakeMicroBatchStream(schema, FakeOption(option))
  }
}



