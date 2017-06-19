package com.cloudera.spark.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.HBaseTestingUtility
import org.apache.hadoop.hbase.client.Delete
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.HConnectionManager
import org.apache.hadoop.hbase.client.Increment
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Finders
import org.scalatest.FunSuite

class HBaseContextSuite extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll {

//  var htu: HBaseTestingUtility = null

  val tableName = "cxf1"
  val columnFamily = "info"

  var sc:SparkContext = null;

  override def beforeAll() {
//    htu = HBaseTestingUtility.createLocalHTU()
//    
//    htu.cleanupTestDir()
//    println("starting minicluster")
//    htu.startMiniZKCluster();
//    htu.startMiniHBaseCluster(1, 1);
//    println(" - minicluster started")
//    try {
//      htu.deleteTable(Bytes.toBytes(tableName))
//    } catch {
//      case e: Exception => {
//        println(" - no table " + tableName + " found")
//      }
//    }
//    println(" - creating table " + tableName)
//    htu.createTable(Bytes.toBytes(tableName), Bytes.toBytes(columnFamily))
//    println(" - created table")

    val sparkConfig = new SparkConf();
    sparkConfig.set("spark.broadcast.compress", "false");
    sc = new SparkContext("local", "test", sparkConfig)
  }

  override def afterAll() {
//    htu.deleteTable(Bytes.toBytes(tableName))
//    println("shuting down minicluster")
//    htu.shutdownMiniHBaseCluster()
//    htu.shutdownMiniZKCluster()
//    println(" - minicluster shut down")
//    htu.cleanupTestDir()

    sc.stop();
  }

  def getConfiguration() : Configuration ={
    val config = new Configuration();

    config.set("hbase.zookeeper.quorum", "bdp-dev-3:2181");
    config.set("zookeeper.znode.parent", "/hbase-webank");
    config
  }
  
  test("bulkput to test HBase client") {
    val config = getConfiguration();
    
    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("1"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo1")))),
      (Bytes.toBytes("2"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("b"), Bytes.toBytes("foo2")))),
      (Bytes.toBytes("3"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("c"), Bytes.toBytes("foo3")))),
      (Bytes.toBytes("4"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("d"), Bytes.toBytes("foo")))),
      (Bytes.toBytes("5"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("e"), Bytes.toBytes("bar"))))))

    val hbaseContext = new HBaseContext(sc, config);

    hbaseContext.bulkPut[(Array[Byte], Array[(Array[Byte], Array[Byte], Array[Byte])])](rdd,
      tableName,
      (putRecord) => {

        val put = new Put(putRecord._1)
        putRecord._2.foreach((putValue) => put.add(putValue._1, putValue._2, putValue._3))
        put

      },
      true);

    val connection = HConnectionManager.createConnection(config)
    val htable = connection.getTable(Bytes.toBytes(tableName))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("1"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("a")).
      getValue()).equals("foo1"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("2"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("b")).
      getValue()).equals("foo2"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("3"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("c")).
      getValue()).equals("foo3"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("4"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("d")).
      getValue()).equals("foo"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("5"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("e")).
      getValue()).equals("bar"))

    
  }

  test("bulkput to test HBase client fs storage of Config") {
    val config = getConfiguration();

    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("1x"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo1")))),
      (Bytes.toBytes("2x"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("b"), Bytes.toBytes("foo2")))),
      (Bytes.toBytes("3x"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("c"), Bytes.toBytes("foo3")))),
      (Bytes.toBytes("4x"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("d"), Bytes.toBytes("foo")))),
      (Bytes.toBytes("5x"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("e"), Bytes.toBytes("bar"))))))

    val tmpPath = "tmp/HBaseConfig"

    val fs = FileSystem.newInstance(new Configuration())
    if (fs.exists(new Path(tmpPath))) {
      fs.delete(new Path(tmpPath), false)
    }

    val hbaseContext = new HBaseContext(sc, config, tmpPath);

    hbaseContext.bulkPut[(Array[Byte], Array[(Array[Byte], Array[Byte], Array[Byte])])](rdd,
      tableName,
      (putRecord) => {

        val put = new Put(putRecord._1)
        putRecord._2.foreach((putValue) => put.add(putValue._1, putValue._2, putValue._3))
        put

      },
      true);

    val connection = HConnectionManager.createConnection(config)
    val htable = connection.getTable(Bytes.toBytes(tableName))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("1x"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("a")).
      getValue()).equals("foo1"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("2x"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("b")).
      getValue()).equals("foo2"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("3x"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("c")).
      getValue()).equals("foo3"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("4x"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("d")).
      getValue()).equals("foo"))

    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("5x"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("e")).
      getValue()).equals("bar"))


  }

  test("bulkIncrement to test HBase client") {
    val config =getConfiguration();

    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("1"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("counter"), 1L))),
      (Bytes.toBytes("2"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("counter"), 2L))),
      (Bytes.toBytes("3"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("counter"), 3L))),
      (Bytes.toBytes("4"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("counter"), 4L))),
      (Bytes.toBytes("5"), Array((Bytes.toBytes(columnFamily), Bytes.toBytes("counter"), 5L)))))

    val hbaseContext = new HBaseContext(sc, config);

    hbaseContext.bulkIncrement[(Array[Byte], Array[(Array[Byte], Array[Byte], Long)])](rdd,
      tableName,
      (incrementRecord) => {
        val increment = new Increment(incrementRecord._1)
        incrementRecord._2.foreach((incrementValue) =>
          increment.addColumn(incrementValue._1, incrementValue._2, incrementValue._3))
        increment
      },
      4);

    hbaseContext.bulkIncrement[(Array[Byte], Array[(Array[Byte], Array[Byte], Long)])](rdd,
      tableName,
      (incrementRecord) => {
        val increment = new Increment(incrementRecord._1)
        incrementRecord._2.foreach((incrementValue) =>
          increment.addColumn(incrementValue._1, incrementValue._2, incrementValue._3))
        increment
      },
      4);

    val connection = HConnectionManager.createConnection(config)
    val htable = connection.getTable(Bytes.toBytes(tableName))

    assert(Bytes.toLong(htable.get(new Get(Bytes.toBytes("1"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("counter")).
      getValue()) == 2L)

    assert(Bytes.toLong(htable.get(new Get(Bytes.toBytes("2"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("counter")).
      getValue()) == 4L)

    assert(Bytes.toLong(htable.get(new Get(Bytes.toBytes("3"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("counter")).
      getValue()) == 6L)

    assert(Bytes.toLong(htable.get(new Get(Bytes.toBytes("4"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("counter")).
      getValue()) == 8L)

    assert(Bytes.toLong(htable.get(new Get(Bytes.toBytes("5"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("counter")).
      getValue()) == 10L)
    
  }

  test("bulkDelete to test HBase client") {
    val config = getConfiguration();

    val connection = HConnectionManager.createConnection(config)
    val htable = connection.getTable(Bytes.toBytes(tableName))

    var put = new Put(Bytes.toBytes("delete1"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo1"))
    htable.put(put)
    put = new Put(Bytes.toBytes("delete2"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo2"))
    htable.put(put)
    put = new Put(Bytes.toBytes("delete3"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo3"))
    htable.put(put)

    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("delete1")),
      (Bytes.toBytes("delete3"))))

    val hbaseContext = new HBaseContext(sc, config);

    hbaseContext.bulkDelete[Array[Byte]](rdd,
      tableName,
      putRecord => new Delete(putRecord),
      4);

    assert(htable.get(new Get(Bytes.toBytes("delete1"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("a")) == null)
    assert(htable.get(new Get(Bytes.toBytes("delete3"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("a")) == null)
    assert(Bytes.toString(htable.get(new Get(Bytes.toBytes("delete2"))).
      getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes("a")).
      getValue()).equals("foo2"))
      
  }

  test("bulkGet to test HBase client") {
    val config = getConfiguration();

    config.set("spark.broadcast.compress", "false");

    val connection = HConnectionManager.createConnection(config)
    val htable = connection.getTable(Bytes.toBytes(tableName))

    var put = new Put(Bytes.toBytes("get1"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo1"))
    htable.put(put)
    put = new Put(Bytes.toBytes("get2"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo2"))
    htable.put(put)
    put = new Put(Bytes.toBytes("get3"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo3"))
    htable.put(put)

    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("get1")),
      (Bytes.toBytes("get2")),
      (Bytes.toBytes("get3")),
      (Bytes.toBytes("get4"))))


    val hbaseContext = new HBaseContext(sc, config);

    val getRdd = hbaseContext.bulkGet[Array[Byte], Object](
      tableName,
      2,
      rdd,
      record => {
        new Get(record)
      },
      (result: Result,t:Array[Byte]) => {
        if (result.list() != null) {
          val it = result.list().iterator()
          val B = new StringBuilder
  
          B.append(Bytes.toString(result.getRow()) + ":")
  
          while (it.hasNext()) {
            val kv = it.next()
            val q = Bytes.toString(kv.getQualifier())
            if (q.equals("counter")) {
              B.append("(" + Bytes.toString(kv.getQualifier()) + "," + Bytes.toLong(kv.getValue()) + ")")
            } else {
              B.append("(" + Bytes.toString(kv.getQualifier()) + "," + Bytes.toString(kv.getValue()) + ")")
            }
          }
          "" + B.toString
        } else {
          ""
        }
      })

      
    val getArray = getRdd.collect
    
    getArray.foreach(f => println(f));

    assert(getArray.length == 4)
    assert(getArray.contains("get1:(a,foo1)"))
    assert(getArray.contains("get2:(a,foo2)"))
    assert(getArray.contains("get3:(a,foo3)"))

  }
  
  test("distributedScan to test HBase client") {
    val config = getConfiguration();

    config.set("spark.broadcast.compress", "false");

    val connection = HConnectionManager.createConnection(config)
    val htable = connection.getTable(Bytes.toBytes(tableName))

    var put = new Put(Bytes.toBytes("scan1"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo1"))
    htable.put(put)
    put = new Put(Bytes.toBytes("scan2"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo2"))
    htable.put(put)
    put = new Put(Bytes.toBytes("scan3"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo3"))
    htable.put(put)
    put = new Put(Bytes.toBytes("scan4"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo3"))
    htable.put(put)
    put = new Put(Bytes.toBytes("scan5"))
    put.add(Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("foo3"))
    htable.put(put)

    var scan = new Scan()
    scan.setCaching(100)
    scan.setStartRow(Bytes.toBytes("scan2"))
    scan.setStopRow(Bytes.toBytes("scan4_"))

    val hbaseContext = new HBaseContext(sc, config);

    val scanRdd = hbaseContext.hbaseRDD(tableName, scan)
    
    val scanList = scanRdd.collect
    
    //assert(scanList.length == 3)
    
  }

}