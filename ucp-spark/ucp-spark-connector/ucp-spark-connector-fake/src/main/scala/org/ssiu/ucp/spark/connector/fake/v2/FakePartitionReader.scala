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
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReader, PartitionReaderFactory}
import org.apache.spark.sql.types.{DataType, IntegerType, StructType}
import org.apache.spark.unsafe.types.UTF8String

import scala.util.Random


/**
 * Use in [[FakeBatch.planInputPartitions]] && [[FakeMicroBatchStream.planInputPartitions]] method
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
 * Generate only [[FakeOption.recordPer]] data items
 *
 * @param schema Fake data schema
 */
class FakePartitionReader(schema: StructType, fakeOption: FakeOption)
  extends PartitionReader[InternalRow] {

  import FakePartitionReader._

  // Thread safety?
  private var current = 0

  override def next(): Boolean = {
    if (current < fakeOption.recordPer) {
      current += 1
      true
    } else {
      false
    }
  }

  override def get(): InternalRow = {
    InternalRow.fromSeq(schemaToSeq(schema, fakeOption))
  }

  override def close(): Unit = {
    // nothing to do now
  }
}

object FakePartitionReader {
  /**
   * Convert StructType to a line of randomly generated data
   *
   * @return Seq[Any]
   */
  private[v2] def schemaToSeq(schema: StructType, fakeOption: FakeOption) = {
    schema.map(_.dataType).map(t => typeToValue(t, fakeOption))
  }

  /**
   * Generate random data by type.
   *
   * @note we must use [[UTF8String]] when generate a string
   */
  private[v2] def typeToValue(dataType: DataType, fakeOption: FakeOption): Any = {
    dataType match {
      case IntegerType => Random.nextInt(fakeOption.intMax)
      case _ => UTF8String.fromString(Random.alphanumeric.take(1 + Random.nextInt(fakeOption.strMaxLen)).mkString)
    }
  }
}