package my.learning.ml.selectors

/**
 * 卡方检验 χ2 。概率论教材P233
 * https://en.wikipedia.org/wiki/Chi-squared_test
 */
import org.apache.spark.ml.feature.ChiSqSelector
import org.apache.spark.ml.linalg.Vectors
// $example off$
import org.apache.spark.sql.SparkSession

object ChiSqSelectorExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("ChiSqSelectorExample")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    // $example on$
    val data = Seq(
      (7, Vectors.dense(0.0, 0.0, 18.0, 1.0), 1.0),
      (8, Vectors.dense(0.0, 1.0, 12.0, 0.0), 0.0),
      (9, Vectors.dense(1.0, 0.0, 15.0, 0.1), 0.0))

    val df = spark.createDataset(data).toDF("id", "features", "clicked")

    val selector = new ChiSqSelector()
      .setNumTopFeatures(1)
      .setFeaturesCol("features")
      .setLabelCol("clicked")
      .setOutputCol("selectedFeatures")

    val result = selector.fit(df).transform(df)

    println(s"ChiSqSelector output with top ${selector.getNumTopFeatures} features selected")
    result.show()
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
