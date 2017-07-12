package my.learning.ml.transform

import org.apache.spark.ml.feature.PolynomialExpansion
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import scala.reflect.api.materializeTypeTag

object PolynomialExpansionExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("PolynomialExpansionExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val data = Array(
      Vectors.dense(2.0, 1.0),
      Vectors.dense(0.0, 0.0),
      Vectors.dense(3.0, -1.0))
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    df.select("features").show(false)
    
    val polyExpansion = new PolynomialExpansion()
      .setInputCol("features")
      .setOutputCol("polyFeatures")
      .setDegree(3)

    val polyDF = polyExpansion.transform(df)
    polyDF.show(false)
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
