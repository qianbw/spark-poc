package my.learning.ml.regression

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.SparkSession

/**
 * Binomial logistic regression
 */
object LogisticRegressionWithElasticNetExample {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("LogisticRegressionWithElasticNetExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    // Load training data
    val training = spark.read.format("libsvm").load("/Users/qianbw/git/spark/data/mllib/sample_libsvm_data.txt")

    //    val training = spark.createDataFrame(Seq(
    //      (0, Vectors.dense(1.0, 1.0, 3, 4, 5)),
    //      (1, Vectors.dense(1.0, -1.0, 5, 4, 5)),
    //      (2, Vectors.dense(-1.0, -1.0, 3, 4, 5)),
    //      (3, Vectors.dense(-1.0, 1.0, 3, 4, 8)))).toDF("label", "features")

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    // Fit the model
    val lrModel = lr.fit(training)

    // Print the coefficients and intercept for logistic regression
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    // We can also use the multinomial family for binary classification
    val mlr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
      .setFamily("multinomial")

    val mlrModel = mlr.fit(training)

    // Print the coefficients and intercepts for logistic regression with multinomial family
    println(s"Multinomial coefficients: ${mlrModel.coefficientMatrix}")
    println(s"Multinomial intercepts: ${mlrModel.interceptVector}")
    // $example off$

    spark.stop()
  }
}
// scalastyle:on println
