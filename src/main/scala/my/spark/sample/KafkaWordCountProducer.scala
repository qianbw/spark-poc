package my.spark.sample

import java.util.Properties

import kafka.producer.KeyedMessage
import kafka.producer.Producer
import kafka.producer.ProducerConfig

// Produces some random words between 1 and 100.
object KafkaWordCountProducer {

  def main(args: Array[String]) {

    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCountProducer <metadataBrokerList> <topic> <messagesPerSec> <wordsPerMessage>")
      System.exit(1)
    }

    val Array(brokers, topic, messagesPerSec, wordsPerMessage) = args

    // Zookeeper connection properties
    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")

    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    // Send some messages
    while (true) {
      val messages = (1 to messagesPerSec.toInt).map { messageNum =>
        val str = (1 to wordsPerMessage.toInt).map(x => scala.util.Random.nextInt(10).toString).mkString(" ")

        new KeyedMessage[String, String](topic, "mykey", str)
      }.toArray

      messages.map(x => println(x))
      producer.send(messages: _*)
      Thread.sleep(1000)
    }
  }
}