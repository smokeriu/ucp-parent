package org.ssiu.ucp.spark.core.service

import org.apache.spark.sql.DataFrame
import org.ssiu.ucp.core.service.TableProvider

import java.util.Optional
import scala.collection.mutable

case class SparkTableProvider() extends TableProvider[DataFrame] {

  private val cache = mutable.HashMap[String, DataFrame]()

  override def getTable(name: String): Optional[DataFrame] = {
    Optional.ofNullable(cache.get(name).orNull)
  }

  override def addTable(name: String, t: DataFrame): Unit = {
    // register every element to spark memory view
    t.createOrReplaceTempView(name)
    cache.put(name, t)
  }
}
