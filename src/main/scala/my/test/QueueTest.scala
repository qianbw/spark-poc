package my.test

import scala.collection.mutable.Queue
import scala.util.Random

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object QueueTest {
  def main(args: Array[String]) {
    val queueClass = new QueueClass

    for (i <- 1 to 10) {
      val elem = Random.nextInt(100).toString();
      queueClass.enqueue(elem)
    }

    for (i <- 1 to 10) {
      queueClass.dequeue()
    }
  }
}

class QueueClass {
  private val logger: Logger = LoggerFactory.getLogger(getClass)
  private val connectionPool = Queue[String]()

  def enqueue(elem: String) = {
    connectionPool.enqueue(elem)
    //logger.info("enqueue:" + elem + ", size:" + connectionPool.size)
    System.out.println("enqueue:" + elem + ", size:" + connectionPool.size)

  }

  def dequeue() = {
    val elem = connectionPool.dequeue
    //logger.info("dequeue, elem:" + elem + ", remainSize:" + connectionPool.size)
    System.out.println("dequeue:" + elem + ", size:" + connectionPool.size)
  }
}
