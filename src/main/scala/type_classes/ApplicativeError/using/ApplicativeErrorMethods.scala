package type_classes.ApplicativeError.using

import cats.ApplicativeError
import cats.implicits._
import cats.effect.IO

object ApplicativeErrorMethods extends App {

  /** raiseError: <p>
   *  Lift an error into the F context.
   * */
  def divide[F[_]](dividend: Int, divisor: Int)(implicit F: ApplicativeError[F, String]): F[Int] =
    if (divisor == 0) then F.raiseError("division by zero")
    else F.pure(dividend / divisor)

  type ErrorOr[A] = Either[String, A]

  divide[ErrorOr](6, 3)       // res: ErrorOr[Int] = Right(2)
  divide[ErrorOr](6, 0)       // res: ErrorOr[Int] = Left(division by zero)



  /** raiseWhen: <br>
   *  Returns raiseError when the cond is true, otherwise F.unit
   *
   *  raiseUnless: <br>
   *  Returns raiseError when cond is false, otherwise F.unit
   * */
  val condition = true

  IO.raiseWhen(condition)(new IllegalArgumentException("Too many"))       // IllegalArgumentException("Too many")
  IO.raiseUnless(condition)(new IllegalArgumentException("Too many"))     // IO[Unit]

  
  // TODO: Other methods, e.g. onError() ...
}
