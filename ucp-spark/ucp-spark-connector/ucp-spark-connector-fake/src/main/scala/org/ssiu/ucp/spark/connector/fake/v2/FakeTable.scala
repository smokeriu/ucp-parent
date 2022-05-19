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
   * Support BATCH_READ & MICRO_BATCH_READnow
   */
  override def capabilities(): util.Set[TableCapability] = {
    (TableCapability.BATCH_READ :: TableCapability.MICRO_BATCH_READ :: TableCapability.CONTINUOUS_READ :: Nil).toSet.asJava
  }

  override def newScanBuilder(caseInsensitiveStringMap: CaseInsensitiveStringMap): ScanBuilder = {
    new FakeTableScanBuilder(userSchema, caseInsensitiveStringMap)
  }
}

object FakeTable {
  final val NAME = "ucp.fake"
}
