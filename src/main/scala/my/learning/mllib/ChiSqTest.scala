package my.learning.mllib

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.SparkSession

object ChiSqTest {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Test")
      .master("local[*]")
      .getOrCreate()

    val v1 = Vectors.dense(43.0, 9.0) // 观测值
    val v2 = Vectors.dense(44.0, 4.0) // 理论值
    val c1 = Statistics.chiSqTest(v1, v2)
    println(c1)

    spark.stop()
  }
}
// scalastyle:on println
