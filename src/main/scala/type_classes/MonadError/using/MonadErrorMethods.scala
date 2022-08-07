package type_classes.MonadError.using

import cats.MonadError
import cats.implicits.*
import cats.effect.IO
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.global

import scala.util.{Failure, Success, Try}

object MonadErrorMethods extends App {

  // TODO DELETE DEBUG
  extension[A] (io: IO[A]) {
    def debug: IO[A] = io.map { value =>
      println(s"[${Thread.currentThread().getName}] $value")
      value
    }
  }
  /** ensure: <p>
   * Turns a successful value into an error if it does not satisfy a given predicate.
   * */
  val a = IO(42)

  MonadError[IO, Throwable].ensure(a)(throw new IllegalStateException())(_ != 43)   // IO(42)
  MonadError[IO, Throwable].ensure(a)(throw new IllegalStateException())(_ != 42)   // IllegalStateException


  /** ensureOr: <p>
   * Turns a successful value into an error specified by the error function if it does not satisfy a given predicate.
   *
   * E.g. same as ensure, but can save the value error msg
   * */
  val b = IO(42)
  MonadError[IO, Throwable].ensureOr(b)(e => throw new IllegalStateException(e.toString))(_ != 42)    // IllegalStateException: 42


  /** rethrow: <p>
   * Inverse of attempt
   *  */
  val c: Try[Either[Throwable, Int]] = Success(Left(new java.lang.Exception))
  c.rethrow             // res: Try[Int] = Failure(java.lang.Exception)

  val d: Try[Either[Throwable, Int]] = Success(Right(1))
  d.rethrow             // res: Try[Int] = Success(1)
  
  
  /** attemptTap: <p>
   *  Reifies the value or error of the source and performs an effect on the result, then recovers the original value or error back into F. <br>
   *  Note that if the effect returned by f fails, the resulting effect will fail too. <br>
   *  Alias for fa.attempt.flatTap(f).rethrow for convenience.
   * */
  def checkError(result: Either[Throwable, Int]): Try[String] =
    result.fold(_ => Failure(new java.lang.Exception), _ => Success("success"))
  
  val f: Try[Int] = Failure(new Throwable("failed"))
  f.attemptTap(checkError)            // res: Try[Int] = Failure(java.lang.Exception)
  
  val g: Try[Int] = Success(1)
  g.attemptTap(checkError)            // res: Try[Int] = Success(1)
  
  
}
