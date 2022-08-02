package datatypes.ValidatedDemo

/* PARALLEL VALIDATION IN A SYSTEM - Converting Validation to an Applicative */

import cats.{Applicative, Apply, Semigroup, SemigroupK}
import cats.data.{NonEmptyChain, Validated, ValidatedNec}
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.*
import simulacrum.typeclass


object ParallelValidation_v2 extends App {

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
  final case class MissingConfig(field: String) extends ConfigError     // When the key can't be found, in Config's Map
  final case class ParseError(field: String) extends ConfigError        // When the value for this key is invalid, in Config's Map


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

  // For withEither() method
  def positive(field: String, i: Int): Either[ConfigError, Int] = {
    if (i >= 0) Right(i)
    else Left(ParseError(field))
  }

  // ====================================================================================================================



  /* Convert Validated to Applicative:
  *   To access Applicative's map method, which will allow us to chain validations (in personFromConfig)     */

  given given_validatedApplicative[E : Semigroup]: Applicative[Validated[E, *]] with {
    def ap[A, B](f: Validated[E, A => B])(fa: Validated[E, A]): Validated[E, B] = (fa, f) match {
      case (Valid(a), Valid(fab)) => Valid(fab(a))
      case (i@Invalid(_), Valid(_)) => i
      case (Valid(_), i@Invalid(_)) => i
      case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup[E].combine(e1, e2))
    }

    def pure[A](x: A): Validated[E, A] = Validated.valid(x)
  }


  case class Address(houseNumber: Int, street: String)
  case class Person(name: String, age: Int, address: Address)


  // Validates a Config
  def validatePerson(personConfig: Config): ValidatedNec[ConfigError, Person] = Apply[ValidatedNec[ConfigError, *]].map4(
    personConfig.parse[String]("name").toValidatedNec,
    personConfig.parse[Int]("age").toValidatedNec,

    personConfig.parse[Int]("house_number").andThen {       // Using andThen() method
      n => if (n >= 0) then Validated.valid(n) else Validated.invalid(ParseError("house_number HEHE"))
    }.toValidatedNec,

//    personConfig.parse[Int]("house_number").withEither {  // Using withEither() method
//      either: Either[ConfigError, Int] => {
//        either.flatMap(i => positive("house_number", i))
//      } }.toValidatedNec,

    personConfig.parse[String]("street").toValidatedNec
  ) { case (name, age, houseNumber, street) => Person(name, age, Address(houseNumber, street)) }




  // TESTING:
  val person1 = Config(Map(("name", "cat"), ("age", "not a number"), ("house_number", "5"), ("lane", "feline street")))
  println(validatePerson(person1))    // Invalid(Chain(MissingConfig(street), ParseError(age)))

  val person2 = Config(Map(("NAME", "cat"), ("age", "53"), ("house_number", "HEHE"), ("street", "feline street")))
  println(validatePerson(person2))    // Invalid(Chain(ParseError(house_number), MissingConfig(name)))

  val person3 = Config(Map(("name", "cat"), ("age", "53"), ("house_number", "5"), ("street", "feline street")))
  println(validatePerson(person3))    // Valid(Person(cat,53,Address(5,feline street)))




  /*  andThen() method:
        Similar to flatMap.
        In the case of success, it passes the valid value into a function that returns a new Validated instance.

      withEither() method:
        Temporarily turns a Validated instance into an Either instance and apply it to a function.
        Can use Monad functionality (e.g. flatMap & tailRecM) that Either has, on a Validated         */


}
