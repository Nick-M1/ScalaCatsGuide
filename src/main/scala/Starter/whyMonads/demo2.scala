package Starter.whyMonads

/* Example #1: User-Form */

object demo2 {

  case class Person(firstName: String, lastName: String) {
    assert(firstName != null && lastName != null) // Requirement that these fields must not be nulls
  }

  // #1 - Java Defensive Style -> Difficult to read
  def getPerson1(firstName: String, lastName: String): Person =
    if (firstName != null) {
      if (lastName != null) {
        Person(firstName.capitalize, lastName.capitalize)
      } else {
        null
      }
    } else {
      null
    }

  // #2 - Scala style using Monads & flatMap
  def getPerson2(firstName: String, lastName: String): Option[Person] =
    Option(firstName).flatMap { fName =>
      Option(lastName).flatMap { lName =>
        Option(Person(fName, lName))
      }
    }
  
  // #3 - Scala style using Monads & for-comprehension (for-comp uses flatMap)
  def getPerson3(firstName: String, lastName: String): Option[Person] = for {
    fName <- Option(firstName)
    lName <- Option(lastName)
  } yield Person(fName, lName)

}
