package my.spark.sample

import scala.collection.immutable.Queue
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ConnectionPool {
  private val logger: Logger = LoggerFactory.getLogger(getClass)
  private val connectionPool = Queue[Connection]()

  private val hbaseConfig: Configuration = new Configuration();
  hbaseConfig.set("hbase.zookeeper.quorum", "bdp-01:2181,bdp-02:2181,bdp-03:2181");
  hbaseConfig.set("zookeeper.znode.parent", "/hbase-webank");
  hbaseConfig.set("hbase.client.retries.number", "2");
  private val conf = HBaseConfiguration.create(hbaseConfig);

  def getConnection() = {
    // TODO  存在内存溢出风险
    if (connectionPool.size > 0) {
      val (elem, remain) = connectionPool.dequeue
      logger.info("getConnection1, size:" + connectionPool.size)
      elem
    } else {
      val elem = ConnectionFactory.createConnection(conf)
      connectionPool.enqueue(elem)
      logger.info("getConnection2, size:" + connectionPool.size)
      elem
    }
  }

  def returnConnection(elem: Connection) = {
    logger.info("returnConnection")
    connectionPool.enqueue(elem)
  }
}
