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
