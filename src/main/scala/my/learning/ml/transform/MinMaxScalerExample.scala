package my.learning.ml.transform

import org.apache.spark.ml.feature.MinMaxScaler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
 * 每一列的数据运用公式。每一列的最大值一定为1，每一列的最下值一定为0
 */
object MinMaxScalerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("MinMaxScalerExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val dataFrame = spark.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.1, -1.0)),
      (1, Vectors.dense(2.0, 1.1, 1.0)),
      (2, Vectors.dense(3.0, 10.1, 3.0)))).toDF("id", "features")

    val scaler = new MinMaxScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")

    // Compute summary statistics and generate MinMaxScalerModel
    val scalerModel = scaler.fit(dataFrame)

    // rescale each feature to range [min, max].
    val scaledData = scalerModel.transform(dataFrame)
    println(s"Features scaled to range: [${scaler.getMin}, ${scaler.getMax}]")
    scaledData.select("features", "scaledFeatures").show(false)
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
