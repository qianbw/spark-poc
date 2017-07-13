package my.learning.ml.transform

import scala.collection.Seq
import scala.reflect.api.materializeTypeTag

import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
 * 当特征选择完成后，可以直接训练模型了，但是可能由于特征矩阵过大，导致计算量大，训练时间长的问题，因此降低特征矩阵维度也是必不可少的。
 * 常见的降维方法除了以上提到的基于L1惩罚项的模型以外，另外还有主成分分析法（PCA）和线性判别分析（LDA），
 * 线性判别分析本身也是一个分类模型。PCA和LDA有很多的相似点，其本质是要将原始的样本映射到维度更低的样本空间中，
 * 但是PCA和LDA的映射目标不一样：PCA是为了让映射后的样本具有最大的发散性；
 * 而LDA是为了让映射后的样本有最好的分类性能。所以说PCA是一种无监督的降维方法，而LDA是一种有监督的降维方法。
 *
 * 链接：https://www.zhihu.com/question/28641663/answer/110165221
 */
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
