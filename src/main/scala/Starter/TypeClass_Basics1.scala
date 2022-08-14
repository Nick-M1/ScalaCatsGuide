package Starter

/** The 4 main components of using Cats Type-classes: <p>
 * 1) The type class - abstract/generic pattern <br>
 * 2) Instances of this type class for different types 3) Functions/Api that uses these implementations <br>
 * 4) Extension methods - for simpler syntax
 * */
object TypeClass_Basics1 extends App {

  case class Person(name: String, age: Int)     // User-defined class


  /* Type-class definition - Abstract / Generic:
     An interface that represents some functionality that we want to implement.
     Doesn't contain the actual impplementation, just the generic function signitures  */
  trait JSONSerialiser[T] {
    def toJson(value: T): String
  }

  /* Instances:
     Implicit implementations of the type-class for different types     */
  given JSONSerialiser[String] with
    override def toJson(value: String): String = "\"" + value + "\""

  given JSONSerialiser[Int] with
    override def toJson(value: Int): String = value.toString

  given JSONSerialiser[Person] with
    override def toJson(value: Person): String =
      s"""
         |{ "name" : ${value.name}, "age" : ${value.age} }
         |""".stripMargin.trim


  /* API / Function that uses these implicit implementations:
     This function only accepts a list of T, where T is a type that has an instance/implementation of JSONSerialiser (currently, types: String, Int & Person)  */
  def listToJSON[T](list: List[T])(using serialiser: JSONSerialiser[T]): String =
    list.map(v => serialiser.toJson(v)).mkString("[", ", ", "]")


  listToJSON(List( Person("Alice", 23), Person("Dave", 19) ))   // [{ "name" : Alice, "age" : 23 }, { "name" : Dave, "age" : 19 }]
//  listToJSON(List(true, false, true))                         // error -> boolean is not an instance of JSONSerialiser



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
