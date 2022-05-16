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

/**
 * Use in [[FakeBatch.planInputPartitions]] method
 *
 * @param id partition id
 */
class FakeInputPartition(id: Int) extends InputPartition

/**
 * A factory can create [[FakePartitionReader]]
 *
 * @param schema     table schema
 * @param fakeOption fake options
 */
class FakePartitionReaderFactory(schema: StructType, fakeOption: FakeOption) extends PartitionReaderFactory {
  override def createReader(inputPartition: InputPartition): PartitionReader[InternalRow] = {
    new FakePartitionReader(schema, fakeOption)
  }
}

/**
 * We provide a single thread/partition fake reader now.
 *
 * Generate only [[FakeOption.maxRecord]] data items
 *
 * @param schema Fake data schema
 */
class FakePartitionReader(schema: StructType, fakeOption: FakeOption)
  extends PartitionReader[InternalRow] {

  // Thread safety?
  private var current = 0

  override def next(): Boolean = {
    if (current < fakeOption.maxRecord) {
      current += 1
      true
    } else {
      false
    }
  }

  override def get(): InternalRow = {
    InternalRow.fromSeq(schemaToSeq())
  }

  override def close(): Unit = {
    // nothing to do now
  }

  /**
   * Convert StructType to a line of randomly generated data
   *
   * @return Seq[Any]
   */
  private def schemaToSeq() = {
    schema.map(_.dataType).map(typeToValue)
  }

  /**
   * Generate random data by type.
   *
   * @note we must use [[UTF8String]] when generate a string
   */
  private def typeToValue(dataType: DataType): Any = {
    dataType match {
      case IntegerType => Random.nextInt(fakeOption.intMax)
      case _ => UTF8String.fromString(Random.alphanumeric.take(Random.nextInt(fakeOption.strMaxLen)).mkString)
    }
  }
}
