package Case_Studies.DataValidation


import cats.Semigroup
import cats.instances.list.*
import cats.syntax.either.*
import cats.syntax.semigroup.* // for |+|
import cats.data.Validated
import cats.syntax.apply._ // for mapN
import cats.data.Validated._ // for Valid and Invalid


object Demo3 extends App {


  /* Implementation v2:
     Model checks as an algebraic data type, with an explicit data type for each combinator.      */

  // Either has an Applicative instance, but it doesn't have the semantics we want. It fails fast instead of accumulating errors.
  // If we want to accumulate errors Validated is a more appropriate abstraction

  sealed trait Check[E, A] {

    import Check._

    def and(that: Check[E, A]): Check[E, A] =
      And(this, that)

    def or(that: Check[E, A]): Check[E, A] =
      Or(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] = this match {
      case Pure(func) => func(a) // If a isn't an applicative type (e.g Nothing)

      case And(left, right) => (left(a), right(a)).mapN((_, _) => a) // If .and() method used - need the error msg of both left & right if exist (not fail-fast)

      case Or(left, right) => left(a) match { // If .or() method used - need either error msg of left & right (fail-fast okay)
        case Valid(a) => Valid(a) //    If the left item has the error, then return it, else check right item
        case Invalid(e1) => right(a) match {
          case Valid(a) => Valid(a) //    If the right item has an error, return it
          case Invalid(e2) => Invalid(e1 |+| e2) //    Else, both invalid
        }
      }
    }

  }

  object Check {
    final case class And[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]

    final case class Or[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]

    final case class Pure[E, A](func: A => Validated[E, A]) extends Check[E, A]
  }


  /*
  
  // Testing:
  val a: Check[List[String], Int] = Check( v => if(v > 2) then v.asRight else List("Must be > 2").asLeft )
  val b: Check[List[String], Int] = Check( v => if(v < -2) then v.asRight else List("Must be < -2").asLeft)
  val check: Check[List[String], Int] = a and b      // a.and(b) -> this should always give error as a num can't be x < -2 & x > 2

  check(5)        // res: Either[List[String], Int] = Left(List("Must be < -2"))
  check(0)        // res: Either[List[String], Int] = Left(List("Must be > 2", "Must be < -2"))    -> returns both errors




  // Issue with this implementation: create checks that fail with a type that can't accumulate. Below using type Nothing as accumulator
  val c: Check[Nothing, Int] = Check(v => v.asRight)
  val d: Check[Nothing, Int] = Check(v => v.asRight)
  val check2 = c and d        // No error
  
  */
}
