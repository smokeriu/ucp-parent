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

package org.ssiu.ucp.spark.connector.fake.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.junit.jupiter.api.{Assertions, BeforeEach, Test}
import org.ssiu.ucp.spark.connector.fake.v2.{FakeOption, FakeTable}
import org.apache.spark.sql.functions._

import scala.util.Random

@Test
class FakeBatchTest {

  private val fakeConnectorName = FakeTable.NAME

  var spark: SparkSession = _

  @BeforeEach
  def setUp(): Unit = {
    spark = SparkSession.builder().master("local[2]").getOrCreate()
  }

  @Test
  def baseTest(): Unit = {
    val schema = StructType(StructField("id", IntegerType) :: StructField("name", StringType) :: Nil)
    val in = spark.read
      .schema(schema)
      .format(fakeConnectorName)
      .load()
    in.show(false)
  }

  @Test
  def testRecord(): Unit = {
    val schema = StructType(StructField("id", IntegerType) :: Nil)
    val maxRecord = Random.nextInt(20000)
    val in = spark.read
      .schema(schema)
      .option(FakeOption.SPEED, maxRecord)
      .format(fakeConnectorName)
      .load()
    Assertions.assertEquals(maxRecord, in.count())
  }

  @Test
  def testInteger(): Unit = {
    val columName = "id"
    val schema = StructType(StructField(columName, IntegerType) :: Nil)
    val intMax = Random.nextInt(20000)
    val in = spark.read
      .schema(schema)
      .option(FakeOption.NUMBER_MAX, intMax)
      .format(fakeConnectorName)
      .load()
    val maxInFake = in.agg(max(col(columName))).head().getInt(0)
    println(maxInFake)
    Assertions.assertTrue(maxInFake <= intMax)
  }

  @Test
  def testStringLen(): Unit = {
    val columName = "name"
    val schema = StructType(StructField(columName, StringType) :: Nil)
    val strMaxLen = Random.nextInt(10)
    val in = spark.read
      .schema(schema)
      .option(FakeOption.STR_MAX_LEN, strMaxLen)
      .format(fakeConnectorName)
      .load()
    val maxInFake = in.select(length(col(columName)).as(columName)).agg(max(col(columName))).head().getInt(0)
    in.show()
    println(maxInFake)
    Assertions.assertTrue(maxInFake <= strMaxLen)
  }
}
