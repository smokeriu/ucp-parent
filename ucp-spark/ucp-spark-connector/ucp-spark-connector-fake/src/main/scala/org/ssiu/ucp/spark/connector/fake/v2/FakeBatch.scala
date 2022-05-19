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
