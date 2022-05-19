/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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