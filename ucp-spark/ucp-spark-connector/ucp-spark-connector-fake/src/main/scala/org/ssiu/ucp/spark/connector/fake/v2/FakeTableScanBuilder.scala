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

import org.apache.spark.sql.connector.read.streaming.{ContinuousStream, MicroBatchStream}
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

  override def toContinuousStream(checkpointLocation: String): ContinuousStream = {
    new FakeContinuousStream(schema, FakeOption(option))
  }
}



