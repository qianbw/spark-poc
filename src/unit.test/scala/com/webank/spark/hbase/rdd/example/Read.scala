/* Copyright 2015 UniCredit S.p.A.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.webank.spark.hbase.rdd.example

import org.apache.spark.{ SparkContext, SparkConf }

import com.webank.spark.hbase.rdd.HBaseConfig
import com.webank.spark.hbase.rdd.hbase._


object Read extends App {
  val name = "Example of read from HBase table"

  lazy val sparkConf = new SparkConf().setAppName(name).setMaster("local[2]")
  lazy val sc = new SparkContext(sparkConf)
  implicit val config = HBaseConfig( "zookeeper.znode.parent" -> "/hbase-webank",
      "hbase.rootdir" -> "/hbase-webank",
  "hbase.zookeeper.quorum" -> "bdp-dev-3:2181") // Assumes hbase-site.xml is on classpath

  val columns = Map(
    "info" -> Set("save_type", "save_type_count","size")
  )
  
  sc.hbase[String]("file_index", columns)
    .map({ case (k, v) =>
      val cf1 = v("info")
      val col1 = cf1("save_type")
      val col2 = cf1("save_type_count")
      val col3 = cf1("size")

      List(k, col1, col2, col3) mkString "\t"
    })
    .saveAsTextFile("test-output")
}