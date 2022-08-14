package Case_Studies.DataValidation


import cats.Semigroup
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

import cats.Semigroup
import cats.data.Validated
import cats.data.Validated._
import cats.instances.list.*
import cats.syntax.apply.*
import cats.syntax.either.*
import cats.syntax.semigroup.* // for Valid and Invalid
import cats.syntax.validated._

import cats.data.{NonEmptyList, Validated}


object Demo4 extends App {


  /** Implementation v4: <p>
   *  Model checks as an algebraic data type, with an explicit data type for each combinator. <p>
   *
   *  Issue: Before we had    type Check[E, A] = A => Either[E, A] <br>
   *  so an A type is inputted & type A outputted, but want input & output types to be different <br>
   *  Solution: Use Predicate[E, A] AND Check[E, A, B] = A => Either[E, B]
   *  */

  // PREDICATE
  sealed trait Predicate[E, A] {

    import Predicate.*

    // Join 2 checks together - both checks need to pass to not return error
    def and(that: Predicate[E, A]): Predicate[E, A] =
      And(this, that)

    // Join 2 checks together - either check needs to pass to not return error
    def or(that: Predicate[E, A]): Predicate[E, A] =
      Or(this, that)

    // Applies the checks to a value
    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] = this match {
      case Pure(func) => func(a)                                        // Applies a single check function to a

      case And(left, right) => (left(a), right(a)).mapN((_, _) => a)    // Applies 2 check functions to a & returns error if either check fails

      case Or(left, right) => left(a) match {                           // Applies 2 check functions to a & returns error if both check fails
        case Valid(_) => Valid(a)
        case Invalid(e1) => right(a) match {
          case Valid(_) => Valid(a)
          case Invalid(e2) => Invalid(e1 |+| e2)
        }
      }
    }
  }

  object Predicate {
    final case class And[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]
    final case class Or[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]
    
    final case class Pure[E, A](func: A => Validated[E, A]) extends Predicate[E, A]
    
    // Apply checking function - returns a Pure containing the function
    def apply[E, A](f: A => Validated[E, A]): Predicate[E, A] =     
      Pure(f)
    
    // Given error value & checking function - returns a Pure containing the function
    def lift[E, A](err: E, fn: A => Boolean): Predicate[E, A] =
      Pure(a => if (fn(a)) a.valid else err.invalid)
  }


  // CHECK
  sealed trait Check[E, A, B] {

    import Check.*

    def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]

    def map[C](f: B => C): Check[E, A, C] =
      Map[E, A, B, C](this, f)

    def flatMap[C](f: B => Check[E, A, C]): FlatMap[E, A, B, C] =
      FlatMap[E, A, B, C](this, f)

    def andThen[C](next: Check[E, B, C]): Check[E, A, C] =
      AndThen[E, A, B, C](this, next)
  }

  object Check {
    final case class Map[E, A, B, C](check: Check[E, A, B], func: B => C) extends Check[E, A, C] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
        check(a).map(func)
    }

    final case class FlatMap[E, A, B, C](check: Check[E, A, B], func: B => Check[E, A, C]) extends Check[E, A, C] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
        check(a).withEither(_.flatMap(b => func(b)(a).toEither))
    }

    final case class AndThen[E, A, B, C](check: Check[E, A, B], next: Check[E, B, C]) extends Check[E, A, C] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
        check(a).withEither(_.flatMap(b => next(b).toEither))
    }

    final case class Pure[E, A, B](func: A => Validated[E, B]) extends Check[E, A, B] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, B] =
        func(a)
    }

    final case class PurePredicate[E, A](pred: Predicate[E, A]) extends Check[E, A, A] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
        pred(a)
    }

    def apply[E, A](pred: Predicate[E, A]): Check[E, A, A] =
      PurePredicate(pred)

    def apply[E, A, B](func: A => Validated[E, B]): Check[E, A, B] =
      Pure(func)
  }


  
  
  
  // Error from checks:
  type Errors = NonEmptyList[String]

  def error(s: String): NonEmptyList[String] =
    NonEmptyList(s, Nil)

  
  // THE CHECKING FUNCTIONS:
  def longerThan(n: Int): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must be longer than $n characters"),
      str => str.length > n
    )

  val alphanumeric: Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must be all alphanumeric characters"),
      str => str.forall(_.isLetterOrDigit)
    )

  def contains(char: Char): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must contain the character $char"),
      str => str.contains(char)
    )

  def containsOnce(char: Char): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must contain the character $char only once"),
      str => str.count(c => c == char) == 1
    )


  // Validation criteria (uses funcs above):

  /* A username must contain at least four characters and consist entirely of alphanumeric characters */
  val checkUsername: Check[Errors, String, String] = Check(longerThan(3) and alphanumeric)

  /* An email address must contain a single `@` sign. Split the string at the `@`. The string to the left must not be empty.
     The string to the right must be at least three characters long and contain a dot.                */
  val splitEmail: Check[Errors, String, (String, String)] = Check(_.split('@') match {
    case Array(name, domain) => (name, domain).validNel[String]
    case _ => "Must contain a single @ character".invalidNel[(String, String)]
  })
  val checkLeft: Check[Errors, String, String] = Check(longerThan(0))
  val checkRight: Check[Errors, String, String] = Check(longerThan(3) and contains('.'))
  val joinEmail: Check[Errors, (String, String), String] = Check { case (l, r) => (checkLeft(l), checkRight(r)).mapN(_ + "@" + _) }
  val checkEmail: Check[Errors, String, String] = splitEmail andThen joinEmail // joinEmail = joins the Valid msgs together


  // Test with a User:
  final case class User(username: String, email: String)

  def createUser(username: String, email: String): Validated[Errors, User] =
    (checkUsername(username), checkEmail(email)).mapN(User.apply)


  createUser("Noel", "noel@underscore.io") // res: Validated[Errors, User] = Valid(User("Noel", "noel@underscore.io"))
  createUser("", "dave@underscore.io@io") // res: Validated[Errors, User] = Invalid( NonEmptyList("Must be longer than 3 characters", "Must contain a single @ character") )
}
