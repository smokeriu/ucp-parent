package org.ssiu.ucp.spark.connector.jdbc.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger
import org.junit.jupiter.api.{AfterEach, BeforeEach, Test}

@Test
class MysqlV2Test {

  var spark: SparkSession = _

  @BeforeEach
  def setUp(): Unit = {
    spark = SparkSession.builder().master("local[2]").getOrCreate()
  }

  @Test
  def testWrite(): Unit = {
    val in = spark.readStream
      .format("ucp.fake")
      .load

    val query = in.writeStream
      .format("org.ssiu.ucp.spark.connector.jdbc.v2.JDBC")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("url", "jdbc:mysql://localhost:3306/db_test")
      .option("user", "root")
      .option("password", "root")
      .option("tbName", "test01")
      .option("checkpointLocation", "file:///tmp/fake-mysql-stream-test")
      .trigger(Trigger.ProcessingTime(10000))
      .start()


    query.awaitTermination(1000 * 30)

  }

  @AfterEach
  def release(): Unit = {
    spark.stop()
  }

}
