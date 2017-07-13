package my.learning.ml.transform

import org.apache.spark.ml.feature.Bucketizer
// $example off$
import org.apache.spark.sql.SparkSession

object BucketizerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("BucketizerExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val splits = Array(Double.NegativeInfinity, -0.5, 0.0, 0.5, Double.PositiveInfinity)

    val data = Array(-999.9, -0.5, -0.3, 0.0, 0.2, 999.9)
    val dataFrame = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    dataFrame.show(false)

    val bucketizer = new Bucketizer()
      .setInputCol("features")
      .setOutputCol("bucketedFeatures")
      .setSplits(splits)

    // Transform original data into its bucket index.
    val bucketedData = bucketizer.transform(dataFrame)

    println(s"Bucketizer output with ${bucketizer.getSplits.length-1} buckets")
    bucketedData.show()

    spark.stop()
  }
}
// scalastyle:on println

