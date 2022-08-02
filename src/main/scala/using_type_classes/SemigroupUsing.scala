package using_type_classes

import type_class_definitions.SemigroupCreate.SemigroupInstances.given
import type_class_definitions.SemigroupCreate.Semigroup
import type_class_definitions.SemigroupCreate.ShoppingCart

object SemigroupUsing extends App {

  // Without Semigroup companion object
  val intSemigroup1: Semigroup[Int] = implicitly(Semigroup[Int])
  val StringSemigroup1: Semigroup[String] = implicitly(Semigroup[String])

  // With Semigroup companion object
  val IntSemigroup2: Semigroup[Int] = Semigroup[Int]
  val StringSemigroup2: Semigroup[String] = Semigroup[String]
  val ShoppingSemigroup: Semigroup[ShoppingCart] = Semigroup[ShoppingCart]

  val int1: Int = IntSemigroup2.combine(2, 40)                //42
  val str1: String = StringSemigroup2.combine("Sca", "la")    // "Scala"


  ShoppingSemigroup.combine(
    new ShoppingCart(List("HELLO", "WORLD")),
    new ShoppingCart(List("hello", "world"))
  ).items                                       // List(HELLO, WORLD, hello, world)


}
