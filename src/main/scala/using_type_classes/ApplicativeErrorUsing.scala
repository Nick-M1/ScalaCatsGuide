package using_type_classes

import cats.data.Validated

object ApplicativeErrorUsing extends App {



//  // Original error function
//  def attemptDivide(x: Int, y: Int): Either[String, Int] = y match {
//    case 0 => Left("divisor is zero")
//    case _ => Right(x / y)
//  }

  import cats.ApplicativeError
  import cats.implicits._

  // Make function more abstract - to allow different Error-Container types (inc Either)
  def attemptDivide[F[_]](x: Int, y: Int)(using ae: ApplicativeError[F, String]): F[Int] = y match {
    case 0 => ae.raiseError("divisor is zero")                  // Error raised
    case 1 => ae.raiseError("result == numerator")              // Error raised
    case _ => ae.pure(x / y)                                    // No error
  }

  def handler[F[_]](f: F[Int])(using ae: ApplicativeError[F, String]): F[Int] =
    ae.handleError(f) {
      case "divisor is zero"      => -1
      case "result == numerator"  => -2
      case _                      => -3
      // If result of attemptDivide isn't an error -> returns it as it is
    }

  handler(attemptDivide(3, 0))     // Right(-1)
  handler(attemptDivide(10, 2))    // Right(5)







}
