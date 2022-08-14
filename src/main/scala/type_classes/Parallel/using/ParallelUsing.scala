package type_classes.Parallel.using

import cats.data.NonEmptyList
import cats.implicits._

object ParallelUsing extends App {

  case class Name(value: String)

  case class Age(value: Int)

  case class Person(name: Name, age: Age)

  // Validation checks:
  def parse(s: String): Either[NonEmptyList[String], Int] = {
    if (s.matches("-?[0-9]+")) then Right(s.toInt)
    else Left(NonEmptyList.one(s"$s is not a valid integer."))
  }

  def validateAge(a: Int): Either[NonEmptyList[String], Age] = {
    if (a > 18) then Right(Age(a))
    else Left(NonEmptyList.one(s"$a is not old enough"))
  }

  def validateName(n: String): Either[NonEmptyList[String], Name] = {
    if (n.length >= 8) then Right(Name(n))
    else Left(NonEmptyList.one(s"$n Does not have enough characters"))
  }

  // Creating a Person - Validating the 2 inputs:

  // Using toValidated & toEither
  def parsePerson1(ageString: String, nameString: String) = for {
    age <- parse(ageString)
    person <- (validateName(nameString).toValidated, validateAge(age).toValidated)
      .mapN(Person.apply)
      .toEither
  } yield person

  // Using Parallel - easier syntax
  def parsePerson2(ageString: String, nameString: String) = for {
    age <- parse(ageString)
    person <- (validateName(nameString), validateAge(age)).parMapN(Person.apply)
  } yield person


}
