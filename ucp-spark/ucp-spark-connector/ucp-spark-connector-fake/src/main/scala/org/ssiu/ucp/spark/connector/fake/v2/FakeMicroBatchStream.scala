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

import java.util.concurrent.TimeUnit

import org.apache.spark.sql.connector.read.streaming.{MicroBatchStream, Offset}
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReaderFactory}
import org.apache.spark.sql.execution.streaming.LongOffset
import org.apache.spark.sql.types.StructType

/**
 * Create MicroBatchStream by given schema and fakeOption.
 *
 * For test use only
 */
class FakeMicroBatchStream(schema: StructType, fakeOption: FakeOption) extends MicroBatchStream {

  @volatile private var lastTimeMs = 0L

  private val creationTimeMs = System.currentTimeMillis()

  override def latestOffset(): Offset = {
    val now = System.currentTimeMillis()
    if (lastTimeMs < now) {
      lastTimeMs = now
    }
    LongOffset(TimeUnit.MILLISECONDS.toSeconds(lastTimeMs - creationTimeMs))
  }

  override def planInputPartitions(start: Offset, end: Offset): Array[InputPartition] = {
    // TODO: support multiPartition
    Array[InputPartition](new FakeInputPartition(1))
  }

  override def createReaderFactory(): PartitionReaderFactory = {
    new FakePartitionReaderFactory(schema, fakeOption)
  }

  override def initialOffset(): Offset = LongOffset(0L)

  override def deserializeOffset(json: String): Offset = LongOffset(json.toLong)

  override def commit(end: Offset): Unit = {}

  override def stop(): Unit = {}
}