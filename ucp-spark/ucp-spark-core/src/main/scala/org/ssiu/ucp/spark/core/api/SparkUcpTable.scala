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

package org.ssiu.ucp.spark.core.api

import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.api.UcpTable

case class SparkUcpTable() extends UcpTable[DataFrame]

object SparkUcpTable {
  def apply(tableName: String, holdTable: DataFrame): SparkUcpTable = {
    val table = new SparkUcpTable
    table.setHoldTable(holdTable)
    table.setName(tableName)
    table
  }
}