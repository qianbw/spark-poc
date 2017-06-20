package my.test

import scala.collection.immutable.Queue
import util.Random;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object QueueTest {
  def main(args: Array[String]) {
    val queueClass = new QueueClass
    val elem = Random.nextInt(100).toString();
    queueClass.enqueue(elem)
  }
}

class QueueClass {
  private val logger: Logger = LoggerFactory.getLogger(getClass)
  private val connectionPool = Queue[String]()

  def enqueue(elem: String) = {
    System.out.println(elem)
    connectionPool.enqueue(elem)
    logger.info("enqueue:" + elem + ", size:" + connectionPool.size)
  }

  def dequeue() = {
    val (elem, remain) = connectionPool.dequeue
    logger.info("dequeue, elem:" + elem + ", remain:" + remain)
  }
}
