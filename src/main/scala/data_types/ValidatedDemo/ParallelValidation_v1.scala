package data_types.ValidatedDemo

/* PARALLEL VALIDATION IN A SYSTEM:
*  Our goal is to report any and all errors across independent bits of data.
*  For instance, when we ask for several pieces of configuration, each configuration field can be validated separately from one another.
*  How then do we enforce that the data we are working with is independent? We ask for both of them up front.
*
*  This demo is about config parsing. Our config will be represented by a Map[String, String].
*  Parsing will be handled by a Read type class - we provide instances just for String and Int for brevity.
*
*  NOTE: Parallel validation only if each piece is independent
* */

import simulacrum.typeclass         // for @typeclass
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.Applicative
import cats.Semigroup
import cats.SemigroupK
import cats.data.NonEmptyChain
import cats.implicits._


object ParallelValidation_v1 extends App {

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

  // Parallel Validator
  def parallelValidate[E : Semigroup, A, B, C](v1: Validated[E, A], v2: Validated[E, B])(f: (A, B) => C): Validated[E, C] = (v1, v2) match {
    case (Valid(a), Valid(b))       => Valid(f(a, b))         // both checks returned as valid -> return both as valid
    case (Valid(_), i@Invalid(_))   => i                      // 1 check returned as valid -> return this 1 invalid error
    case (i@Invalid(_), Valid(_))   => i
    case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup[E].combine(e1, e2))  // Both checks were invalid -> combine them together & return. Could do |+|
  }


  // TESTING - Setup:
  case class ConnectionParams(url: String, port: Int)
  given Semigroup[NonEmptyChain[ConfigError]] = SemigroupK[NonEmptyChain].algebra[ConfigError]    // alias for given??




  // TESTING:
  val config1: Config = Config(Map(("endpoint", "127.0.0.1"), ("port", "not an int")))

  parallelValidate(
    config1.parse[String]("url").toValidatedNec,
    config1.parse[Int]("port").toValidatedNec
  )(ConnectionParams.apply)
  // Validated[NonEmptyChain[ConfigError], ConnectionParams] = Invalid( Chain(MissingConfig(url), ParseError(port)) )


  parallelValidate(
    config1.parse[String]("endpoint").toValidatedNec,
    config1.parse[Int]("port").toValidatedNec
  )(ConnectionParams.apply)
  // Validated[NonEmptyChain[ConfigError], ConnectionParams] = Invalid(Chain(ParseError(port)))


  val config2: Config = Config(Map(("endpoint", "127.0.0.1"), ("port", "1234")))

  parallelValidate(
    config2.parse[String]("endpoint").toValidatedNec,
    config2.parse[Int]("port").toValidatedNec
  )(ConnectionParams.apply)
  // Validated[NonEmptyChain[ConfigError], ConnectionParams] = Valid(ConnectionParams(127.0.0.1,1234))


}
