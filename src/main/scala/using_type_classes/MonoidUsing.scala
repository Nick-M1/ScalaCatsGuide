package using_type_classes

import cats.Monoid
import cats.instances.*
import cats.syntax.*
import cats.syntax.group.catsSyntaxSemigroup

object MonoidUsing extends App {
  
  
  
  // Empty method - identity
  5 |+| Monoid[Int].empty                                // 5
  "Hello" |+| Monoid[String].empty                       // "Hello"
  Set(1, 2) |+| Monoid[Set[Int]].empty |+| Set(3, 4)     // Set(1, 2, 3, 4)



  // Exploiting Associativity law

  // foldLeft & foldRight should give same result
  List(1, 2, 3).foldLeft(0)(_ |+| _)   // 6
  List(1, 2, 3).foldRight(0)(_ |+| _)  // 6

  // Also, if we split a list apart & sum the parts in parallel,
  // then gather the results together at the end, this should give the same result
  val (left, right) = List(1, 2, 3, 4, 5).splitAt(2)

  val sumLeft: Int = left.foldLeft(0)(_ |+| _)     // 3
  val sumRight: Int = right.foldLeft(0)(_ |+| _)   // 12
  val result: Int = sumLeft |+| sumRight           // 15



  def combineAll[A: Monoid](as: List[A]): A =
    as.foldLeft(Monoid[A].empty)(Monoid[A].combine)

  import cats.implicits._
  combineAll(List(1, 2, 3))                       // 6
  combineAll(List("hello", " ", "world"))         // "hello world"
  combineAll(List(Set(1, 2), Set(2, 3, 4, 5)))    // Set(5, 1, 2, 3, 4)
  combineAll(List(Map('a' -> 1), Map('a' -> 2, 'b' -> 3), Map('b' -> 4, 'c' -> 5)))
    // Map('b' -> 7, 'c' -> 5, 'a' -> 3)



  // CATS BUILT-IN:

  Monoid[Int].combineAll(List(1, 2, 3))                           // 6
  Monoid[Set[Int]].combineAll(List(Set(1, 2), Set(2, 3, 4, 5)))   // Set(5, 1, 2, 3, 4)

  Monoid[Map[Char, Int]].combineAll(
    List(Map('a' -> 1), Map('a' -> 2, 'b' -> 3), Map('b' -> 4, 'c' -> 5))
  )       // Map('b' -> 7, 'c' -> 5, 'a' -> 3)



  // TODO: If want to use generic in a given-with, how do you do this without using a name (for below)


  // EXAMPLE - Monoid Functionality to Semigroups
  import cats.Semigroup
  import cats.implicits._

  final case class NonEmptyList[A](head: A, tail: List[A]) {
    def ++(other: NonEmptyList[A]): NonEmptyList[A] = NonEmptyList(head, tail ++ other.toList)
    def toList: List[A] = head :: tail
  }

  given nonEmptyListSemigroup[A]: Semigroup[NonEmptyList[A]] with
    override def combine(x: NonEmptyList[A], y: NonEmptyList[A]): NonEmptyList[A] = x ++ y


  // Solution - Use Option Monoid

  // Option implementation of Monoid
//  given optionMonoid[A: Semigroup]: Monoid[Option[A]] with {
//    def empty: Option[A] = None
//
//    def combine(x: Option[A], y: Option[A]): Option[A] = x match {
//        case None => y
//        case Some(x_val) => y match {
//            case None => x
//            case Some(y_val) => Some(x_val |+| y_val)
//          }
//      }
//  }

  val list: List[NonEmptyList[Int]] = List(NonEmptyList(1, List(2, 3)), NonEmptyList(4, List(5, 6)))
  val lifted: List[Option[NonEmptyList[Int]]] = list.map(nel => Option(nel))
  Monoid.combineAll(lifted)       // Some(NonEmptyList(1,List(2, 3, 4, 5, 6)))
}
