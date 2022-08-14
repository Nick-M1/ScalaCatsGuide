package data_types.EitherDemo

import cats.syntax.all._
import cats.implicits._

object EitherDemo3 {


  //  val either: Either[NumberFormatException, Int] =
  //    try {
  //      Either.right("abc".toInt)
  //    } catch {
  //      case nfe: NumberFormatException => Either.left(nfe)
  //    }

  // Catch specific errors (in this case: NumberFormatException)
  val eitherSpecific: Either[NumberFormatException, Int] =
    Either.catchOnly[NumberFormatException]("abc".toInt)

  // Catch all errors
  val eitherAll: Either[Throwable, Int] =
    Either.catchNonFatal("abc".toInt)

}
