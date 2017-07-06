package my.learning.chapter6

import my.learning.chapter6.TrafficLightColor.Red
import my.learning.chapter6.TrafficLightColor.Yellow

class Person {
  def doWhat(color: TrafficLightColor.Value) = {
    if (color == Red) "stop"
    else if (color == Yellow) "hurry up"
    else "go"
  }
}