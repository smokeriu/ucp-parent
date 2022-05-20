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

package org.ssiu.ucp.spark.core.service

import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.service.TableProvider
import java.util.Optional

import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv

import scala.collection.mutable

case class SparkTableProvider() extends TableProvider[SparkRuntimeEnv, DataFrame] {

  private val cache = mutable.HashMap[String, DataFrame]()

  override def getTable(env: SparkRuntimeEnv, name: String): Optional[DataFrame] = {
    Optional.ofNullable(cache.get(name).orNull)
  }

  override def addTable(env: SparkRuntimeEnv,name: String,  t: DataFrame): Unit = {
    // register every element to spark memory view
    t.createOrReplaceTempView(name)
    cache.put(name, t)
  }
}
