package data_types.EitherT

import cats.data.EitherT
import cats.implicits.*
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/* EitherT is used when EitherT placed in effectful types such as Option or Future,
*   otherwise using Either will require lots of boilerplate code
*   For example: using EitherT[Future, String, Double] instead of Future[Either[String, Double]]
*
*  EitherT[F[_], A, B] is a lightweight wrapper for F[Either[A, B]]
*  If F is a Monad, then EitherT will also form a Monad (with Monad methods like flatMap)
* */

object Demo1 {

  // Non-asynchronous functions/processes
  def parseDouble(s: String): Either[String, Double] =                    // Try converting to Double.  map() only work if Try succeeded, so return Either as a Right
    Try(s.toDouble).map(Right(_)).getOrElse(Left(s"$s is not a number"))  //   else if map() fails, then Try failed, so will return Either as a Left (fail)

  def divide(a: Double, b: Double): Either[String, Double] =
    Either.cond(b != 0, a / b, "Cannot divide by zero")


  // Wrappers around previous functions to make them asynchronous (concurrent-safe)
  def parseDoubleAsync(s: String): Future[Either[String, Double]] =
    Future.successful(parseDouble(s))

  def divideAsync(a: Double, b: Double): Future[Either[String, Double]] =
    Future.successful(divide(a, b))


  // Main function
  def divisionProgramAsync(inputA: String, inputB: String): EitherT[Future, String, Double] = for {
    a <- EitherT(parseDoubleAsync(inputA))
    b <- EitherT(parseDoubleAsync(inputB))
    result <- EitherT(divideAsync(a, b))
  } yield result


  // Testing:
  divisionProgramAsync("4", "2").value    // res: Future[Either[String, Double]] = Future(Success(Right(2.0)))
  divisionProgramAsync("a", "b").value    // res: Future[Either[String, Double]] = Future(Success(Left(a is not a number)))

}
