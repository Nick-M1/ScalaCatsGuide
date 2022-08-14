package data_types.EitherDemo

import cats.syntax.all._
import cats.implicits._

object EitherDemo extends App {


  5.asRight[String] // Either[String, Int] = Right(5)
  "error".asLeft[Int] // Either[String, Int] = Left("error")

  // Alternative syntax:
  val e1: Either[String, Int] = Right(5) // Either[String, Int] = Right(5)
  val _ = e1.map(_ + 1) // Either[String, Int] = Right(6)

  val e2: Either[String, Int] = Left("hello") // Either[String, Int] = Left("hello")
  val _ = e2.map(_ + 1) // Either[String, Int] = Left("hello")

  val _ = e2.left.map(_ + 1) // Either[String, Int] = Left(hello1)



  // EXAMPLE #1
  // Functions to pass a string to an integer -> take reciprocal -> return reciprocal as string

  object ExceptionStyle {

    def parse(s: String): Int =
      if (s.matches("-?[0-9]+")) s.toInt
      else throw new NumberFormatException(s"${s} is not a valid integer.")

    def reciprocal(i: Int): Double =
      if (i == 0) throw new IllegalArgumentException("Cannot take reciprocal of 0.")
      else 1.0 / i

    def stringify(d: Double): String = d.toString
  }

  object EitherStyle {

    private def parse(s: String): Either[Exception, Int] =
      if (s.matches("-?[0-9]+")) Either.right(s.toInt)
      else Either.left(new NumberFormatException(s"${s} is not a valid integer."))

    private def reciprocal(i: Int): Either[Exception, Double] =
      if (i == 0) Either.left(new IllegalArgumentException("Cannot take reciprocal of 0."))
      else Either.right(1.0 / i)

    private def stringify(d: Double): String = d.toString

    def reciprocalStr(s: String): String =
      parse(s).flatMap(reciprocal).map(stringify) match { // matches Either[Exception, String]
        case Left(_: NumberFormatException) => "not a number!"
        case Left(_: IllegalArgumentException) => "can't take reciprocal of 0!"
        case Left(_) => "got unknown exception"
        case Right(s) => s"Got reciprocal: $s"
      }
  }

  object EitherCustomExceptions {

    // Custom exceptions
    sealed abstract class Error

    final case class NotANumber(string: String) extends Error

    case object NoZeroReciprocal extends Error

    private def parse(s: String): Either[Error, Int] =
      if (s.matches("-?[0-9]+")) Either.right(s.toInt)
      else Either.left(NotANumber(s))

    private def reciprocal(i: Int): Either[Error, Double] =
      if (i == 0) Either.left(NoZeroReciprocal)
      else Either.right(1.0 / i)

    private def stringify(d: Double): String = d.toString

    // Main function that gets called
    def reciprocalStr(s: String): String =
      parse(s).flatMap(reciprocal).map(stringify) match {
        case Left(NotANumber(char)) => s"$char a number!"
        case Left(NoZeroReciprocal) => "can't take reciprocal of 0!"
        case Right(s) => s"Got reciprocal: $s"
      }

    reciprocalStr("123") // "Got reciprocal: 0.008130081300813009"

  }

  import EitherCustomExceptions.*


}
