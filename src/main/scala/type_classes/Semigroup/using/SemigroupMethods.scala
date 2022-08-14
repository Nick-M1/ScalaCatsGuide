package type_classes.Semigroup.using

import cats.{Semigroup, Monoid}
import cats.implicits._
import cats.syntax.semigroup._

object SemigroupMethods {
  
  /** combine: <p>
   * Associative operation which combines two values. 
   * */
  Semigroup[Int].combine(2, 40)                       // res: Int = 42
  Semigroup[String].combine("Hello ", "World!")       // res: String = Hello World!
  Semigroup[Option[Int]].combine(None, Some(1))       // res: Option[Int] = Some(1)

  // combine on our user-defined ShoppingCart class:
  import type_classes.Semigroup.implementations.SemigroupImplementations.ShoppingCart
  import type_classes.Semigroup.implementations.SemigroupImplementations.SemigroupInstances.given
  
  Semigroup[ShoppingCart].combine(
    new ShoppingCart(List("carrot", "bread")),
    new ShoppingCart(List("eggs", "milk"))
  ).items             // List("carrot", "bread", "eggs", "milk")

  
  
  // Special syntax for combine on 2 Semigroups (from Cats)

  5 |+| 10                                   // 15
  "Hi " |+| "there" |+| Monoid[String].empty // "Hi there"
  ("hello", 10) |+| (" world", 15)           // ("hello world", 25)  -> = (tuple1._1 ++ tuple2._2,  tuple1._1 + tuple2._2)

  Map("hello" -> 1, "world" -> 1) |+| Map("hello" -> 2, "cats" -> 3)
  // Map("world" -> 1, "hello" -> 3, "cats"  -> 3)
  // "hello" appears in both Maps, so it's values get added together
  

}
