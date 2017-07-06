package my.learning.chapter6

object Test extends App {
  if (args.length > 0) {
    println("hello world!" + args(0))
  } else {
    println("hello!")
  }

  val person = new Person
  println(person.doWhat(TrafficLightColor.Red))

  for (c <- TrafficLightColor.values) {
    println(c.id + ":" + c)
  }

  println(TrafficLightColor(0))
  println(TrafficLightColor.withName("Yellow"))
}