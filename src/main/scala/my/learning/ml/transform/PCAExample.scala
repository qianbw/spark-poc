package my.learning.ml.transform

import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import scala.collection.Seq
import scala.reflect.api.materializeTypeTag

object PCAExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("PCAExample")
      .master("local[*]")
      .getOrCreate()

    // 等价于下面的Array
    val data = Array(
      Vectors.sparse(5, Seq((1, 1.0), (3, 7.0))),
      Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
      Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0))

    //    val data = Array(
    //      Vectors.dense(0, 1.0, 0, 7.0, 0),
    //      Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
    //      Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0))

    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    df.select("features").show(false)

    val pca = new PCA()
      .setInputCol("features")
      .setOutputCol("pcaFeatures")
      .setK(3)
      .fit(df)

    val result = pca.transform(df).select("pcaFeatures")
    result.show(false)
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
