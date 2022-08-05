package Case_Studies.DataValidation

import cats.Semigroup
import cats.instances.list.*
import cats.syntax.either.*
import cats.syntax.semigroup.* // for |+|


object Demo2 extends App {


  /** Implementation v2: <p>
     Model checks as an algebraic data type, with an explicit data type for each combinator.      */
  sealed trait Check[E, A] {

    import Check._

    def and(that: Check[E, A]): Check[E, A] =
      And(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Either[E, A] = this match {
      case Pure(func) => func(a)                                // If type is not applicative (e.g. Nothing type)
      case And(left, right) => (left(a), right(a)) match {      // If type is applicative
        case (Left(e1), Left(e2)) => (e1 |+| e2).asLeft
        case (Left(e), Right(_)) => e.asLeft
        case (Right(_), Left(e)) => e.asLeft
        case (Right(_), Right(_)) => a.asRight
      }
    }
  }

  object Check {
    final case class And[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]

    final case class Pure[E, A](func: A => Either[E, A]) extends Check[E, A]

    def pure[E, A](f: A => Either[E, A]): Check[E, A] =
      Pure(f)
  }


  // Testing:
  val a: Check[List[String], Int] = Check.pure(v => if (v > 2) then v.asRight else List("Must be > 2").asLeft)
  val b: Check[List[String], Int] = Check.pure(v => if (v < -2) then v.asRight else List("Must be < -2").asLeft)
  val check: Check[List[String], Int] = a and b // a.and(b) -> this should always give error as a num can't be x < -2 & x > 2

  check(5) // res: Either[List[String], Int] = Left(List("Must be < -2"))
  check(0) // res: Either[List[String], Int] = Left(List("Must be > 2", "Must be < -2"))    -> returns both errors


  // Issue with this implementation: create checks that fail with a type that can't accumulate. Below using type Nothing as accumulator
  val c: Check[Nothing, Int] = Check.pure(v => v.asRight)
  val d: Check[Nothing, Int] = Check.pure(v => v.asRight)
  val check2 = c and d // No error
}
