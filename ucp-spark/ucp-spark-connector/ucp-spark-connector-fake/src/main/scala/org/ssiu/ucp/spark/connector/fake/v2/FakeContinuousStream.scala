package org.ssiu.ucp.spark.connector.fake.v2

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.InputPartition
import org.apache.spark.sql.connector.read.streaming._
import org.apache.spark.sql.execution.streaming.LongOffset
import org.apache.spark.sql.types.StructType

// TODO: support multiPartition

/**
 * A single partition fake-stream
 */
class FakeContinuousStream(schema: StructType, fakeOption: FakeOption) extends ContinuousStream {

  private val creationTime: Long = System.currentTimeMillis()

  override def planInputPartitions(start: Offset): Array[InputPartition] = {
    Array[InputPartition](new FakeInputPartition(1))
  }

  override def createContinuousReaderFactory(): ContinuousPartitionReaderFactory = {
    new FakeContinuousPartitionReaderFactory(fakeOption, creationTime, schema)
  }

  override def mergeOffsets(offsets: Array[PartitionOffset]): Offset = {
    //    assert(offsets.length == numPartitions)
    val offsetSum = offsets.map(of => of.asInstanceOf[FakePartitionOffset])
      .map(_.nextReadTime).sum
    LongOffset(offsetSum)
  }

  override def initialOffset(): Offset = LongOffset(0L)

  override def deserializeOffset(json: String): Offset = LongOffset(json.toLong)

  override def commit(end: Offset): Unit = {}

  override def stop(): Unit = {}
}

class FakeContinuousPartitionReaderFactory(fakeOption: FakeOption, startTimeMs: Long, schema: StructType) extends ContinuousPartitionReaderFactory {
  override def createReader(partition: InputPartition): ContinuousPartitionReader[InternalRow] = {
    new FakeContinuousPartitionReader(fakeOption, startTimeMs, schema)
  }
}

class FakeContinuousPartitionReader(fakeOption: FakeOption, startTimeMs: Long, schema: StructType)
  extends ContinuousPartitionReader[InternalRow] {

  private var nextReadTime: Long = startTimeMs
  private val readTimeIncrement: Long = 1000 / fakeOption.recordPer

  override def getOffset: PartitionOffset = FakePartitionOffset(nextReadTime)

  override def next(): Boolean = {
    nextReadTime += readTimeIncrement

    try {
      var toWaitMs = nextReadTime - System.currentTimeMillis
      while (toWaitMs > 0) {
        Thread.sleep(toWaitMs)
        toWaitMs = nextReadTime - System.currentTimeMillis
      }
    } catch {
      case _: InterruptedException =>
        // Someone's trying to end the task; just let them.
        return false
    }
    true
  }

  override def get(): InternalRow = InternalRow.fromSeq(FakePartitionReader.schemaToSeq(schema, fakeOption))

  override def close(): Unit = {}
}

case class FakeOffset()

case class FakePartitionOffset(nextReadTime: Long) extends PartitionOffset {

}