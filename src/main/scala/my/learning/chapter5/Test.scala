package my.learning.chapter5

object Test {
  def main(args: Array[String]) {
    val myCounter = new Counter
    myCounter.increment()
    println(myCounter.current())

    val myPerson = new Person
    myPerson.age = 30
    println(myPerson.age)

    myPerson.age = 21
    println(myPerson.age)
  }
}