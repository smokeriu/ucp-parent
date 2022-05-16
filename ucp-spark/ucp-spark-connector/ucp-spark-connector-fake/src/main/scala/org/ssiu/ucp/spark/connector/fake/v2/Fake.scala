package org.ssiu.ucp.spark.connector.fake.v2

import java.util

import org.apache.spark.sql.connector.catalog.{Table, TableProvider}
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.sources.DataSourceRegister
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.util.CaseInsensitiveStringMap

/**
 * Used to generate simulation data based on the schema given by the user.
 *
 * inferSchema give a default schema for this connector
 */
class Fake() extends TableProvider with DataSourceRegister {

  override def inferSchema(caseInsensitiveStringMap: CaseInsensitiveStringMap): StructType = {
    StructType(StructField("id", IntegerType) :: StructField("name", StringType) :: Nil)
  }

  /**
   * Get table by the given structType
   *
   * @param structType user specify schema
   * @return [[FakeTable]]
   */
  override def getTable(structType: StructType, transforms: Array[Transform], map: util.Map[String, String]): Table = {
    new FakeTable(structType)
  }

  /**
   * support use read.schema() method
   */
  override def supportsExternalMetadata(): Boolean = true

  /**
   * Use [[FakeTable.NAME]] as the connector alias
   */
  override def shortName(): String = FakeTable.NAME
}

