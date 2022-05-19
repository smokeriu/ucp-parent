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

package org.ssiu.ucp.spark.operator.code.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expression.CodeInvokeColumn
import org.apache.spark.sql.functions.{col, sum}
import org.junit.jupiter.api.{Assertions, BeforeEach, Test}

@Test
class CodeInvokeColumnTest {

  var spark: SparkSession = _


  @BeforeEach
  def setUp(): Unit = {
    spark = SparkSession.builder().master("local[2]").getOrCreate()
  }

  @Test
  def intTypeTest(): Unit = {
    val env = spark
    import env.implicits._
    val input = spark.sparkContext.parallelize(1 :: 2 :: 3 :: 4 :: Nil).toDF("id")
    val methodName = "add"
    val returnType = "int"
    val code =
      s"""
         |public $returnType $methodName(int id){
         |   return id + 1;
         |}
         |""".stripMargin

    val out = input.withColumn("hello", CodeInvokeColumn.codeInvoke(code, methodName, returnType, col("id")))
      .select("hello")
      .agg(sum(col("hello")))
      .head()
      .getLong(0) // sum make int to long

    println(out)
    Assertions.assertEquals(14, out)
  }

  @Test
  def StringTest(): Unit = {
    val env = spark
    import env.implicits._
    val input = spark.sparkContext.parallelize("1" :: "2" :: "3" :: "4" :: Nil).toDF("id")
    val methodName = "add"
    val returnType = "String"
    val code =
      s"""
         |public $returnType $methodName(String id){
         |   return id + "1";
         |}
         |""".stripMargin

    val out = input.withColumn("hello", CodeInvokeColumn.codeInvoke(code, methodName, returnType, col("id")))
      .select("hello")
      .collect().map(_.getString(0))
    println(out.mkString(","))
    out.foreach(x => {
      Assertions.assertTrue(x.endsWith("1"))
    })
  }

  @Test
  def TwoArgTest(): Unit = {
    val env = spark
    import env.implicits._
    val input = spark.sparkContext.parallelize((1, 1) :: Nil).toDF("id", "id2")
    val methodName = "add"
    val returnType = "String"
    val code =
      s"""import java.util.Map;
         |public $returnType $methodName(int id, int id2){
         |   return String.valueOf(id + id2);
         |}
         |""".stripMargin

    val out = input.withColumn("hello", CodeInvokeColumn.codeInvoke(code, methodName, returnType, col("id"), col("id2")))
      .select("hello")
      .head()
      .getString(0)

    println(out)
    Assertions.assertEquals("2", out)
  }

  @Test
  def EmptyArgTest(): Unit = {
    val env = spark
    import env.implicits._
    val input = spark.sparkContext.parallelize((1, 1) :: Nil).toDF("id", "id2")
    val methodName = "add"
    val returnType = "String"
    val code =
      s"""public $returnType $methodName(){
         |   return "hello world";
         |}
         |""".stripMargin

    val out = input.withColumn("hello", CodeInvokeColumn.codeInvoke(code, methodName, returnType))
      .select("hello")
      .head()
      .getString(0)

    println(out)
    Assertions.assertEquals("hello world", out)
  }
}
