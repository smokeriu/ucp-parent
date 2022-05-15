package org.ssiu.ucp.spark.core.service

import java.util.Optional

import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.service.TableProvider

import scala.collection.mutable

case class SparkTableProvider() extends TableProvider[DataFrame] {

  private val cache = mutable.HashMap[String, DataFrame]()

  override def getTable(name: String): Optional[DataFrame] = {
    Optional.ofNullable(cache.get(name).orNull)
  }

  override def addTable(name: String, t: DataFrame): Unit = {
    cache.put(name, t)
  }
}
