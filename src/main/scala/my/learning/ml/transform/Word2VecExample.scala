package my.learning.ml.transform

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.annotation.Experimental
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.annotation.Since
import scala.collection.Seq
import scala.reflect.api.materializeTypeTag

object Word2VecExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("Word2Vec example")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    // Input data: Each row is a bag of words from a sentence or document.
    val documentDF = spark.createDataFrame(Seq(
      "Hi I heard about Spark".split(" "),
      "I wish Java could use case classes".split(" "),
      "Logistic regression models are neat".split(" ")).map(Tuple1.apply)).toDF("text")

    // add log by qianbw
    documentDF.select("text").take(3).foreach(println)
    // end log by qianbw

    // Learn a mapping from words to Vectors.
    val word2Vec = new Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(5)
      .setMinCount(0)
    val model = word2Vec.fit(documentDF)

    val result = model.transform(documentDF)
    result.collect().foreach {
      case Row(text: Seq[_], features: Vector) =>
        println(s"Text: [${text.mkString(", ")}] => \nVector: $features\n")
    }
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
