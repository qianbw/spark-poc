package my.spark.sample.util

import java.util.Properties

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import kafka.producer.Producer
import kafka.producer.ProducerConfig

object KafkaProducer {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private var instance: Producer[String, String] = null

  /**
   * 只支持生成对同一个brokers列表，同一个topic的producer
   */
  def getInstance(brokers: String): Producer[String, String] = {
    if (null == instance) {
      logger.info("11111")
      val props = new Properties()
      props.put("metadata.broker.list", brokers)
      props.put("serializer.class", "kafka.serializer.StringEncoder")
      val config = new ProducerConfig(props)
      instance = new Producer[String, String](config)
    } else {
      logger.info("22222")
    }
    instance
  }
}
