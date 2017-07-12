package my.learning.ml.transform

import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.ml.feature.CountVectorizerModel
import org.apache.spark.sql.SparkSession
import scala.collection.Seq
import scala.reflect.api.materializeTypeTag

object CountVectorizerExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("CountVectorizerExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val df = spark.createDataFrame(Seq(
      (0, Array("a", "b", "c")),
      (1, Array("a", "b", "b", "c", "a")))).toDF("id", "words")

    // fit a CountVectorizerModel from the corpus
    val cvModel: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .setVocabSize(3)
      .setMinDF(2)
      .fit(df)

    // alternatively, define CountVectorizerModel with a-priori vocabulary
    val cvm = new CountVectorizerModel(Array("a", "b", "c"))
      .setInputCol("words")
      .setOutputCol("features")

    val rst = cvModel.transform(df)
    rst.show(false)

    // add log by qianbw
    rst.select("words", "features").take(3).foreach(println)
    // end log by qianbw

    spark.stop()
  }
}
// scalastyle:on println


