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

    // Function to chain error-checking functions together 
    def and(that: Check[E, A]): Check[E, A] =
      And(this, that)

    // Function to apply a check to a value & return Either
    def apply(a: A)(implicit s: Semigroup[E]): Either[E, A] = this match {
      case Pure(func) => func(a)                                // If type is not applicative (e.g. Nothing type) - Is a single checking function
      case And(left, right) => (left(a), right(a)) match {      // If type is applicative                         - Is 2 checking functions joined together
        case (Left(e1), Left(e2)) => (e1 |+| e2).asLeft             // Both checks returned errors -> combine & return these errors
        case (Left(e), Right(_)) => e.asLeft                        // Only left check returned error -> return only left check
        case (Right(_), Left(e)) => e.asLeft                        // Only right check returned error -> return only right check
        case (Right(_), Right(_)) => a.asRight                      // Neither check returned error -> return input
      }
    }
  }

  object Check {
    final case class And[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]     // Combines 2 checks together
    final case class Pure[E, A](func: A => Either[E, A]) extends Check[E, A]                  // A single check

    def pure[E, A](f: A => Either[E, A]): Check[E, A] =
      Pure(f)
  }


  // Testing:
  val a: Check[List[String], Int] = Check.pure(v => if (v > 2) then v.asRight else List("Must be > 2").asLeft)
  val b: Check[List[String], Int] = Check.pure(v => if (v < -2) then v.asRight else List("Must be < -2").asLeft)
  val check: Check[List[String], Int] = a and b // a.and(b) -> this should always give error as a num can't be x < -2 & x > 2

  check(5) // res: Either[List[String], Int] = Left(List("Must be < -2"))
  check(0) // res: Either[List[String], Int] = Left(List("Must be > 2", "Must be < -2"))    -> returns both errors


  // Issue fixed with this implementation: create checks that fail with a type that can't accumulate. Below using type Nothing as accumulator
  val c: Check[Nothing, Int] = Check.pure(v => v.asRight)
  val d: Check[Nothing, Int] = Check.pure(v => v.asRight)
  val check2 = c and d // No error
}
