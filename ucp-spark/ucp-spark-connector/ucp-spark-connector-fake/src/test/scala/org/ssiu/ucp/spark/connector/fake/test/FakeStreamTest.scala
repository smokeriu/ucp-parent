package org.ssiu.ucp.spark.connector.fake.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.junit.jupiter.api.{BeforeEach, Test}
import org.ssiu.ucp.spark.connector.fake.v2.FakeTable

/**
 * test for micro batch
 */
@Test
class FakeStreamTest {
  private val fakeConnectorName = FakeTable.NAME

  var spark: SparkSession = _

  @BeforeEach
  def setUp(): Unit = {
    spark = SparkSession.builder().master("local[2]").getOrCreate()
  }

  @Test
  def baseMicroBatchTest(): Unit = {
    val schema = StructType(StructField("id", IntegerType) :: StructField("name", StringType) :: Nil)

    val in = spark.readStream
      .schema(schema)
      .format(fakeConnectorName).load()

    val query = in.writeStream
      .format("console")
      .start()

    query.awaitTermination(1000 * 20)
  }

  @Test
  def baseContinuousTest(): Unit = {
    val schema = StructType(StructField("id", IntegerType) :: StructField("name", StringType) :: StructField("info", StringType) :: Nil)

    val in = spark.readStream
      .schema(schema)
      .format(fakeConnectorName).load()

    val query = in.writeStream
      .format("console")
      .trigger(Trigger.Continuous("1 second"))
      .start()

    query.awaitTermination(1000 * 20)
  }
}
