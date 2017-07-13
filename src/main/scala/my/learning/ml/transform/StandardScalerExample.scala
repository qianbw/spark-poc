package my.learning.ml.transform

import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

object StandardScalerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("StandardScalerExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    //val dataFrame = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")
    val dataFrame = spark.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.5, -1.0)),
      (1, Vectors.dense(2.0, 1.0, 1.0)),
      (2, Vectors.dense(4.0, 10.0, 2.0)))).toDF("id", "features")

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true)
      .setWithMean(true)

    // Compute summary statistics by fitting the StandardScaler.
    val scalerModel = scaler.fit(dataFrame)

    // Normalize each feature to have unit standard deviation.
    val scaledData = scalerModel.transform(dataFrame)
    scaledData.show(false)
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
