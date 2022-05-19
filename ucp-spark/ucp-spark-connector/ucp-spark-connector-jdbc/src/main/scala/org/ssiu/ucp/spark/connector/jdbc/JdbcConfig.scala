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

package org.ssiu.ucp.spark.connector.jdbc

import com.typesafe.config.{Config, ConfigFactory}
import org.ssiu.ucp.spark.core.util.ConfigImplicit._

import scala.collection.JavaConverters._

/**
 * a wrapper of JdbcConfig.
 *
 * @param extraOption key -> value
 */
case class JdbcConfig private(url: String,
                              driver: String,
                              tbName: String,
                              user: String,
                              password: String,
                              extraOption: collection.Map[String, String]) {


}

object JdbcConfig {

  def apply(connectInfo: Config): JdbcConfig = {

    val extraOption = connectInfo.optionalConfig(Jdbc.CONNECT_OPTIONS)
      .getOrElse(ConfigFactory.empty())
      .root()
      .asScala
      .map(kv => (kv._1, kv._2.unwrapped().toString))

    val url = if (connectInfo.hasPath(Jdbc.URL)) {
      connectInfo.getString(Jdbc.URL)
    } else {
      val format = connectInfo.getString(Jdbc.FORMAT)
      val host = connectInfo.getString(Jdbc.HOST)
      val port = connectInfo.getInt(Jdbc.PORT)
      val dbName = connectInfo.getString(Jdbc.DB_NAME)
      jdbcUrl(format, host, port, dbName, extraOption)
    }

    val driver = connectInfo.getString(Jdbc.DRIVER)
    val tbName = connectInfo.getString(Jdbc.TB_NAME)
    val user = connectInfo.getString(Jdbc.USER)
    val password = connectInfo.getString(Jdbc.PASSWORD)
    JdbcConfig(url, driver, tbName, user, password, extraOption)
  }

  private def jdbcUrl(format: String, host: String, port: Int, dbName: String, extraOption: collection.Map[String, String]): String = {
    // jdbc:type://host:port/dbName?options
    val option = extraOption.map(kv => s"${kv._1}=${kv._2}").mkString("&")
    val res = format.format(host, port, dbName, option)
    if (extraOption.isEmpty) {
      res.trim.stripSuffix("?")
    }
    res
  }
}
