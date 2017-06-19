package my.spark.sample

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.TableName
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 从kafka读取数据进行处理后，将结果写入到hbase
 */
object FromKafkaToHbase {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: FromKafkaToHbase <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }

    //StreamingExamples.setStreamingLogLevels()
    val logger: Logger = LoggerFactory.getLogger(getClass)
    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("KafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

    //取元祖里面的第二个数, map(_._1) //第一个元素为key
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    lines.print()
    val words = lines.flatMap(_.split(" "))
    // 每隔4s打印前6s的统计信息
    val wordCounts = words.map(x => (x, 1L)).reduceByKeyAndWindow(_ + _, _ - _, Seconds(6), Seconds(4), 3)
    wordCounts.print()

    wordCounts.foreachRDD(rdd => {
      rdd.foreachPartition(partitionRecords => { //循环分区
        try {
          val connection = ConnectionPool.getConnection() //获取HBase连接,分区创建一个连接，分区不跨节点，不需要序列化
          partitionRecords.foreach(s => {
            val key = s._1
            val count = s._2

            val tableName = TableName.valueOf("t1")
            val table = connection.getTable(tableName) //获取表连接

            val put = new Put(Bytes.toBytes(key))
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("count"), Bytes.toBytes(String.valueOf(count)))
            table.put(put)

            table.close() //分区数据写入HBase后关闭连接
          })
        } catch {
          case e: Exception => logger.error("写入HBase失败，{}", e.getMessage)
        }
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}


