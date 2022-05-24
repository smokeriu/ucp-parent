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

import org.apache.spark.sql.streaming.StreamingQuery
import org.slf4j.LoggerFactory
import org.ssiu.ucp.core.service.StreamQueryHandle
import org.ssiu.ucp.spark.core.env.SparkRuntimeEnv

import scala.collection.mutable.ListBuffer

/**
 * Cache all spark stream query, as we can block the program after all the queries have been started
 */
case class SparkQueryHandle() extends StreamQueryHandle[SparkRuntimeEnv, StreamingQuery] {

  private final val LOG = LoggerFactory.getLogger(classOf[SparkQueryHandle])

  private val cache = ListBuffer[StreamingQuery]()

  /**
   * Add Query to cache.
   */
  override def cacheQuery(env: SparkRuntimeEnv, query: StreamingQuery): Unit = {
    if (query != null) {
      LOG.info(s"Add query: ${query.name} to cache.")
      cache += query
    } else {
      LOG.warn("Ignore add query to cache. It is null")
    }
  }

  /**
   * Execute all queries
   *
   * @param env runtime env
   */
  override def execute(env: SparkRuntimeEnv): Unit = {
    cache.foreach(sq => sq.awaitTermination())
  }
}
