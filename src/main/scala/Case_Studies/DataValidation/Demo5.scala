package Case_Studies.DataValidation


import cats.Semigroup
import cats.data.Validated.{Invalid, Valid}
import cats.data.{Kleisli, NonEmptyList, Validated}
import cats.Semigroup
import cats.data.Validated
import cats.data.Validated._
import cats.instances.list.*
import cats.syntax.apply.*
import cats.syntax.either.*
import cats.syntax.semigroup.* // for Valid and Invalid
import cats.syntax.validated._

import cats.data.{NonEmptyList, Validated}


object Demo5 extends App {

  /** USING KLEISLIS (instead of Check) <p>
      Previously, Check was just being used as a wrapper for the functions in Predicate, but can use Kleisli as wrapper instead <br>
      However, Kleislis  relies on the wrapped function returning a monad, so convert Validated[E, A] to Either[E, A]
  */


  // PREDICATE
  sealed trait Predicate[E, A] {
    import Predicate.*

    def run(implicit s: Semigroup[E]): A => Either[E, A] =
      (a: A) => this(a).toEither        // Returns function

    def and(that: Predicate[E, A]): Predicate[E, A] =
      And(this, that)

    def or(that: Predicate[E, A]): Predicate[E, A] =
      Or(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] = this match {
      case Pure(func) => func(a)

      case And(left, right) => (left(a), right(a)).mapN((_, _) => a)

      case Or(left, right) => left(a) match {
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

    def apply[E, A](f: A => Validated[E, A]): Predicate[E, A] =
      Pure(f)

    def lift[E, A](err: E, fn: A => Boolean): Predicate[E, A] =
      Pure(a => if(fn(a)) a.valid else err.invalid)
  }




  // Error from checks:
  type Errors = NonEmptyList[String]              // Use NonEmptyList ,so we can assume it contains at least 1 error if this list is present, so don't need to check if this list is empty

  def error(s: String): NonEmptyList[String] =
    NonEmptyList(s, Nil)

  type Result[A] = Either[Errors, A]
  type Check[A, B] = Kleisli[Result, A, B]

  def check[A, B](func: A => Result[B]): Check[A, B] =
    Kleisli(func)

  def checkPred[A](pred: Predicate[Errors, A]): Check[A, A] =
    Kleisli[Result, A, A](pred.run)


  // Base predicate definitions - Checking Functions

  def longerThan(n: Int): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must be longer than $n characters"),
      str => str.length > n)

  val alphanumeric: Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must be all alphanumeric characters"),
      str => str.forall(_.isLetterOrDigit))

  def contains(char: Char): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must contain the character $char"),
      str => str.contains(char))

  def containsOnce(char: Char): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must contain the character $char only once"),
      str => str.count(c => c == char) == 1)


  // Validation criteria (uses funcs above):

  /* A username must contain at least four characters and consist entirely of alphanumeric characters */
  val checkUsername: Check[String, String] = checkPred(longerThan(3) and alphanumeric)

  /* An email address must contain a single `@` sign. Split the string at the `@`. The string to the left must not be empty.
     The string to the right must be at least three characters long and contain a dot.                */
  val splitEmail: Check[String, (String, String)] = check(_.split('@') match {
    case Array(name, domain) => Right((name, domain))
    case _ => Left(error("Must contain a single @ character"))
  })
  val checkLeft: Check[String, String] = checkPred(longerThan(0))
  val checkRight: Check[String, String] = checkPred(longerThan(3) and contains('.'))
  val joinEmail: Check[(String, String), String] = check { case (l, r) => (checkLeft(l), checkRight(r)).mapN(_ + "@" + _) }
  val checkEmail: Check[String, String] = splitEmail andThen joinEmail      // joinEmail = joins the Valid msgs together



  // Test with a User:
  final case class User(username: String, email: String)

  // Creates a user only if all checking functions pass, otherwise Errors Type is returned with a NonEmptyList of all the errors accumulated
  def createUser(username: String, email: String): Either[Errors, User] = {
    (checkUsername.run(username), checkEmail.run(email)).mapN(User.apply)
  }



  createUser("Noel", "noel@underscore.io")          // res: Either[Errors, User] = Right(User("Noel", "noel@underscore.io"))
  createUser("", "dave@underscore.io@io")           // res:  Either[Errors, User] = Left( NonEmptyList("Must be longer than 3 characters", "Must contain a single @ character") )
}

