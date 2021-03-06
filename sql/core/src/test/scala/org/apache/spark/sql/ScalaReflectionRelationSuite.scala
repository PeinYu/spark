/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql

import org.scalatest.FunSuite

import org.apache.spark.sql.test.TestSQLContext._

case class ReflectData(
    stringField: String,
    intField: Int,
    longField: Long,
    floatField: Float,
    doubleField: Double,
    shortField: Short,
    byteField: Byte,
    booleanField: Boolean,
    decimalField: BigDecimal,
    seqInt: Seq[Int])

case class ReflectBinary(data: Array[Byte])

class ScalaReflectionRelationSuite extends FunSuite {
  test("query case class RDD") {
    val data = ReflectData("a", 1, 1L, 1.toFloat, 1.toDouble, 1.toShort, 1.toByte, true,
                           BigDecimal(1), Seq(1,2,3))
    val rdd = sparkContext.parallelize(data :: Nil)
    rdd.registerAsTable("reflectData")

    assert(sql("SELECT * FROM reflectData").collect().head === data.productIterator.toSeq)
  }

  // Equality is broken for Arrays, so we test that separately.
  test("query binary data") {
    val rdd = sparkContext.parallelize(ReflectBinary(Array[Byte](1)) :: Nil)
    rdd.registerAsTable("reflectBinary")

    val result = sql("SELECT data FROM reflectBinary").collect().head(0).asInstanceOf[Array[Byte]]
    assert(result.toSeq === Seq[Byte](1))
  }
}