package org.ssiu.ucp.spark.connector.fake.v2

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReader, PartitionReaderFactory}
import org.apache.spark.sql.types.{DataType, IntegerType, StructType}
import org.apache.spark.unsafe.types.UTF8String

import scala.util.Random

/**
 * Support data generation in Batch mode
 *
 * @param schema     table schema
 * @param fakeOption fake options
 */
class FakeBatch(schema: StructType, fakeOption: FakeOption) extends Batch {
  /**
   * For now, we only provide single-partition generation mode
   */
  override def planInputPartitions(): Array[InputPartition] = {
    // provide single partition fake source
    Array[InputPartition](new FakeInputPartition(1))
  }

  override def createReaderFactory(): PartitionReaderFactory = {
    new FakePartitionReaderFactory(schema, fakeOption)
  }
}
