package my.learning.ml.regression

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.SparkSession

object MulticlassLogisticRegressionWithElasticNetExample {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("MulticlassLogisticRegressionWithElasticNetExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    // Load training data
    val training = spark
      .read
      .format("libsvm")
      .load("/Users/qianbw/git/spark/data/mllib/sample_multiclass_classification_data.txt")

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    // Fit the model
    val lrModel = lr.fit(training)

    // Print the coefficients and intercept for multinomial logistic regression
    println(s"Coefficients: \n${lrModel.coefficientMatrix}")
    println(s"Intercepts: ${lrModel.interceptVector}")

    spark.stop()
  }
}
// scalastyle:on println
