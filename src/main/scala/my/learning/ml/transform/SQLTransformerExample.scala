package my.learning.ml.transform

import org.apache.spark.ml.feature.SQLTransformer
import org.apache.spark.sql.SparkSession

object SQLTransformerExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("SQLTransformerExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val df = spark.createDataFrame(
      Seq((0, 1.0, 3.0), (2, 2.0, 5.0))).toDF("id", "v1", "v2")

    val sqlTrans = new SQLTransformer().setStatement(
      "SELECT *, (v1 + v2) AS v3, (v1 * v2) AS v4 FROM __THIS__")

    sqlTrans.transform(df).show()
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
