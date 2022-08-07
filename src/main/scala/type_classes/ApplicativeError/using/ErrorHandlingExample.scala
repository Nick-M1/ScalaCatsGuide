package type_classes.ApplicativeError.using

import cats.data.Validated
import cats.ApplicativeError
import cats.implicits._

object ErrorHandlingExample extends App {



  //  // Original error function
  //  def attemptDivide(x: Int, y: Int): Either[String, Int] = y match {
  //    case 0 => Left("divisor is zero")
  //    case _ => Right(x / y)
  //  }


  // Make function more abstract - to allow different Error-Container types (inc Either)
  def attemptDivide[F[_]](x: Int, y: Int)(using ae: ApplicativeError[F, String]): F[Int] = y match {
    case 0 => ae.raiseError("divisor is zero")        // Error raised
    case 1 => ae.raiseError("result == numerator")    // Error raised
    case _ => ae.pure(x / y)                          // No error
  }

  
  // Different error-handling methods:
  
  /** handleError: <p>
   * Handle any error, by mapping it to an A value. */
  def handler1[F[_]](f: F[Int])(using ae: ApplicativeError[F, String]): F[Int] =
    ae.handleError(f) {
      case "divisor is zero" => -1
      case "result == numerator" => -2
      case _ => -3
      // If result of attemptDivide isn't an error -> returns it as it is
    }

  handler1(attemptDivide(3, 0)) // Right(-1)
  handler1(attemptDivide(10, 2)) // Right(5)
  
  
  
  /** handleErrorWith: <p>
   *  Handle any error, potentially recovering from it, by mapping it to an F[A] value. */
  def handler2[F[_]](f: F[Int])(using ae: ApplicativeError[F, String]): F[Int] =
    ae.handleErrorWith(f) {
      case "divisor is zero" => ae.pure(-1)
      case "result == numerator" => ae.pure(-2)
      case _ => ae.pure(-3)
    }
    
    
  /** attempt: <p> 
   * Handle errors by turning them into Either values. <br>
   *  If there is no error, then an scala.util.Right value will be returned instead. <br>
   *  All non-fatal errors should be handled by this method. 
   *  */
  // TODO: Add example


}
