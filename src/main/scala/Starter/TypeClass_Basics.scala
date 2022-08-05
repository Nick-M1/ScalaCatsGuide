package Starter

object TypeClass_Basics extends App {

  case class Person(name: String, age: Int)

  // Type-class definition - Abstract / Generic
  trait JSONSerialiser[T] {
    def toJson(value: T): String
  }

  // Implementations of the type-class for different types
  given JSONSerialiser[String] with
    override def toJson(value: String): String = "\"" + value + "\""

  given JSONSerialiser[Int] with
    override def toJson(value: Int): String = value.toString

  given JSONSerialiser[Person] with
    override def toJson(value: Person): String =
      s"""
         |{ "name" : ${value.name}, "age" : ${value.age} }
         |""".stripMargin.trim


  // API / Function that uses these implementations
  def listToJSON[T](list: List[T])(using serialiser: JSONSerialiser[T]): String =
    list.map(v => serialiser.toJson(v)).mkString("[", ", ", "]")


  listToJSON(List( Person("Alice", 23), Person("Dave", 19) ))   // [{ "name" : Alice, "age" : 23 }, { "name" : Dave, "age" : 19 }]


  // Extension methods to 'add' these methods to existing classes/types (Only applies to classes/types we have already defined JSONSerialiser for, e.g. String, Int & Person)
  extension [T](value: T)(using serialiser: JSONSerialiser[T]) {
    def toJson: String = serialiser.toJson(value)
  }

  Person("Bob", 35).toJson     // { "name" : Bob, "age" : 35 }
  
  println("HELLO".toJson)
  println(6.toJson)
  
//  println(true.toJson)      // -> error has we haven't defined an implementation of JSONSerialiser for type Boolean.




  // Another Extension method, but when the value is wrapped in Option
  extension [T](value: Option[T])(using serialiser: JSONSerialiser[T]) {
    def toJson: String = value match
      case Some(value) => serialiser.toJson(value)      // Unpacks option container & calls toJson implementation on it
      case None => ""                                   // Option empty
  }

  Option(Person("Bob", 35)).toJson      // { "name" : Bob, "age" : 35 }

}
