package data_types.ValidatedDemo

/* PARALLEL VALIDATION IN A SYSTEM - Converting Validation to a Monad
*
*  HOWEVER: This implementation of the Monad's flatMap violates the flatMap consistency law as  fab.ap(fa) != fab.flatMap(f => fa.map(f))
*  */

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyChain, Validated, ValidatedNec}
import cats.implicits.*
import cats.{Applicative, Apply, Semigroup, SemigroupK, Monad}
import simulacrum.typeclass


object ParallelValidation_v3 extends App {

  // SAME AS BEFORE ============================================================================================================

  @typeclass
  trait Read[A] {
    def read(s: String): Option[A]
  }

  object Read {
    def apply[A](implicit A: Read[A]): Read[A] = A

    // String implementation of Read
    given Read[String] with {
      def read(s: String): Option[String] = Some(s)
    }

    // Int implementation of Read
    given Read[Int] with {
      def read(s: String): Option[Int] =
        if (s.matches("-?[0-9]+")) then Some(s.toInt)   // checks that string is an int (via regex), then converts to int
        else None
    }
  }


  // ERRORS:
  sealed abstract class ConfigError       // Base error (abstract, what all actual errors inherit)
  final case class MissingConfig(field: String) extends ConfigError
  final case class ParseError(field: String) extends ConfigError


  // Our parser - Main logic of app
  case class Config(map: Map[String, String]) {
    def parse[A : Read](key: String): Validated[ConfigError, A] = map.get(key) match {
      case None        => Invalid(MissingConfig(key))           // 1st error check
      case Some(value) => Read[A].read(value) match {
        case None    => Invalid(ParseError(key))                // 2nd error check
        case Some(a) => Valid(a)
      }
    }
  }

  // ====================================================================================================================



  /* Convert Validated to MONAD:
  *   To access Monad's flatMap method, which will allow us transform items in Validation wrapper
  *   (We also have access to the Applicative methods as well)                                        */

  given accumulatingValidatedMonad[E: Semigroup]: Monad[Validated[E, *]] with {
    def flatMap[A, B](fa: Validated[E, A])(f: A => Validated[E, B]): Validated[E, B] = fa match {
      case Valid(a)     => f(a)
      case i@Invalid(_) => i
    }

    def pure[A](x: A): Validated[E, A] = Valid(x)

    @annotation.tailrec
    final def tailRecM[A, B](a: A)(f: A => Validated[E, Either[A, B]]): Validated[E, B] = f(a) match {
      case Valid(Right(b)) => Valid(b)
      case Valid(Left(a)) => tailRecM(a)(f)
      case i@Invalid(_) => i
    }

    override def ap[A, B](f: Validated[E, A => B])(fa: Validated[E, A]): Validated[E, B] = (fa, f) match {
      case (Valid(a), Valid(fab)) => Valid(fab(a))
      case (i@Invalid(_), Valid(_)) => i
      case (Valid(_), i@Invalid(_)) => i
      case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup[E].combine(e1, e2))
    }
  }


  // TESTING:

  val personConfig = Config(Map(("name", "cat"), ("age", "not a number"), ("houseNumber", "1234"), ("lane", "feline street")))

  case class Address(houseNumber: Int, street: String)
  case class Person(name: String, age: Int, address: Address)

  val personFromConfig: ValidatedNec[ConfigError, Person] =
    Apply[ValidatedNec[ConfigError, *]].map4(
      personConfig.parse[String]("name").toValidatedNec,
      personConfig.parse[Int]("age").toValidatedNec,
      personConfig.parse[Int]("house_number").toValidatedNec,
      personConfig.parse[String]("street").toValidatedNec
    ) { case (name, age, houseNumber, street) => Person(name, age, Address(houseNumber, street)) }





}
