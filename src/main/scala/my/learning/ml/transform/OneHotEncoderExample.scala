package my.learning.ml.transform

import scala.collection.Seq
import scala.reflect.api.materializeTypeTag

import org.apache.spark.ml.feature.OneHotEncoder
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.SparkSession

/**
 * 是一种状态编码。因为有些特征是用文字分类表达的，或者说将这些类转化为数字，但是数字与数字之间是没有大小关系的，纯粹的分类标记，
 * 这时候就需要用哑编码对其进行编码。比如0用0001,1用0010，2用0100以此类推。有点类似文本矩阵，最终会构成一个稀疏矩阵。
 * 参考：http://blog.csdn.net/big_talent/article/details/53887238
 */
object OneHotEncoderExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("OneHotEncoderExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val df = spark.createDataFrame(Seq(
      (0, "a"),
      (1, "b"),
      (2, "c"),
      (3, "a"),
      (4, "a"),
      (5, "c"))).toDF("id", "category")

    val indexer = new StringIndexer()
      .setInputCol("category")
      .setOutputCol("categoryIndex")
      .fit(df)
    val indexed = indexer.transform(df)
    indexed.show(false)

    val encoder = new OneHotEncoder()
      .setInputCol("categoryIndex")
      .setOutputCol("categoryVec")

    val encoded = encoder.transform(indexed)
    encoded.show()
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
