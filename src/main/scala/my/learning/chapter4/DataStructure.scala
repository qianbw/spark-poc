package my.learning.chapter4

object DataStructure {

  def main(args: Array[String]) {
    val sortedMap = scala.collection.immutable.SortedMap("a" -> 10, "f" -> 7, "b" -> 3, "c" -> 8)
    for ((k, v) <- sortedMap) {
      println(k + ":" + v)
    }
    println("==================================")

    val linkedHashMap = scala.collection.mutable.LinkedHashMap("a" -> 10, "f" -> 7, "b" -> 3, "c" -> 8)
    for ((k, v) <- linkedHashMap) {
      println(k + ":" + v)
    }
    println("==================================")

    val t = (1, 3.14, "hello word")
    println(t._1)
    println("==================================")

    val symbols = Array("a", "b", "c")
    val counts = Array(3, 5, 7)
    val rst1 = symbols.zip(counts)  
    println(rst1.mkString(","))

    val rst2 = rst1.toMap
    println(rst2)
  }
}