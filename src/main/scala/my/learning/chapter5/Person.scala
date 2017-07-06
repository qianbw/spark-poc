package my.learning.chapter5

/**
 * scala中，getter和setter分别叫做age和age_=
 */
class Person {
  private var privateAge = 0

  def age = privateAge

  def age_=(newValue: Int) {
    if (newValue > privateAge) {
      privateAge = newValue
    }
  }
}