package my.spark.sample

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

object WordCountApp {
  def main(args: Array[String]) {

    if (args.length < 1) {
      System.err.println("Usage: <file>")
      System.exit(1)
    }

    val conf = new SparkConf()

    //集群standalone模式
    //conf.setMaster("spark://192.168.80.100:7077").setAppName("MyWordCount")

    //本地调试使用
    conf.setMaster("local[2]").setAppName("MyWordCount")

    val sc = new SparkContext(conf)
    val line = sc.textFile(args(0))

    line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).saveAsTextFile(args(1));

    sc.stop()
  }
}