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

package org.ssiu.ucp.spark.core.util

import com.typesafe.config.Config

object ConfigImplicit {
  implicit class RichConfig(val config: Config) extends AnyVal {
    private def getOptional[T](path: String, get: String => T): Option[T] = {
      if (config.hasPath(path)) {
        Some(get(path))
      } else {
        None
      }
    }

    def optionalString(path: String): Option[String] = getOptional(path, config.getString)

    def optionalLong(path: String): Option[Long] = getOptional(path, config.getLong)

    def optionalInt(path: String): Option[Int] = getOptional(path, config.getInt)

    def optionalDouble(path: String): Option[Double] = getOptional(path, config.getDouble)

    def optionalBoolean(path: String): Option[Boolean] = getOptional(path, config.getBoolean)

    def optionalConfig(path: String): Option[Config] = getOptional(path, config.getConfig)
  }
}
