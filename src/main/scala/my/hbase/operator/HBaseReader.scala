package my.hbase.operator
import scala.collection.mutable.ArrayBuffer

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.client.Scan

object HBaseReader {
  def main(args: Array[String]): Unit = {
    val reader = new HBaseReader("0", "")
    val l = reader.scan("t1")
    //println(l.size)
    l.foreach(println(_))
  }
}

class HBaseReader(val zkUrl: String, val zkRoot: String) {
  val hbaseConfig: Configuration = new Configuration();
  hbaseConfig.set("hbase.zookeeper.quorum", "bdp-01:2181,bdp-02:2181,bdp-03:2181");
  hbaseConfig.set("zookeeper.znode.parent", "/hbase-webank");
  hbaseConfig.set("hbase.client.retries.number", "2");
  val conf = HBaseConfiguration.create(hbaseConfig);

  def apply(): HBaseReader = {
    this()
  }

  def getTable(tablename: String): HTable = {
    new HTable(conf, tablename);
  }

  def scan(tablename: String): ArrayBuffer[Result] = {
    val table = getTable(tablename)
    val s = new Scan();
    s.setCaching(20000);
    s.setBatch(1000);
    val ss = table.getScanner(s);
    //    ss.foreach(println(_))
    val l = ArrayBuffer[Result]()
    val iter = ss.iterator()
    while (iter.hasNext) {
      val value = iter.next()
      //println(value)
      l += value
    }
    table.close();
    l
  }
}