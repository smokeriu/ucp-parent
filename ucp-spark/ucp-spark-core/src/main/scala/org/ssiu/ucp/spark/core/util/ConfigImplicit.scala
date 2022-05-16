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
