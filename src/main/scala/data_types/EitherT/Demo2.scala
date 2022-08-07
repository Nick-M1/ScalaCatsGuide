package data_types.EitherT

import cats.data.EitherT
import cats.implicits._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object Demo2 extends App {

  // From A or B -> EitherT[F, A, B]:
  val num1: EitherT[Option, String, Int] = EitherT.rightT(5)               // A (success) -> EitherT     [could also use EitherT.pure(5) == .rightT(5)]
  val err1: EitherT[Option, String, Int] = EitherT.leftT("Not a number")   // B (error)   -> EitherT

  // From F[A] or F[B] -> EitherT[F, A, B]:
  val num2: EitherT[Option, String, Int] = EitherT.right( Some(5) )        // F[A] (success) -> EitherT
  val err2: EitherT[Option, String, Int] = EitherT.left( Some("error") )   // F[B] (error)   -> EitherT

  // From Either[A, B] or F[Either[A, B]] -> EitherT[F, A, B]:
  val num3: EitherT[List, String, Int] = EitherT.fromEither( Right(100) )  // EitherT.fromEither lifts Either[A, B] -> EitherT[F, A, B]
  val err3: EitherT[List, String, Int] = EitherT.fromEither( Left("Not a number") )
  val num4: EitherT[List, String, Int] = EitherT( List(Right(250)) )       // EitherT constructor converts F[Either[A, B]] -> EitherT[F, A, B]

  // From Option[B] or F[Option[B]] -> EitherT[F, A, B]:
  val option: Option[Int] = None                                                      // None
  val optionList: List[Option[Int]] = List(None, Some(2), Some(3), None, Some(5))     // List(None, Some(2), Some(3), None, Some(5) )

  val op1: EitherT[Future, String, Int] = EitherT.fromOption[Future](option, "option not defined")    // EitherT(Future(Success(Left(option not defined))))
  val op2: EitherT[List, String, Int] = EitherT.fromOptionF(optionList, "option not defined")
      // EitherT(List( Left("option not defined"), Right(2), Right(3), Left("option not defined"), Right(5) )
  val op3: EitherT[List, String, Int] = EitherT.fromOptionM(optionList, List("option not defined"))
      // EitherT(List( Left("option not defined"), Right(2), Right(3), Left("option not defined"), Right(5) )

  /* Option[B]    -> EitherT.fromOption[M] -> EitherT[F, ...]
     F[Option[B]] -> EitherT.fromOptionF   -> EitherT[F, ...], with default as A (if Option == None)
     F[Option[B]] -> EitherT.fromOptionF   -> EitherT[F, ...], with default as F[A] (if Option == None)

     Note: 2nd argument in fromOption...() is the default to put in place if Option == None                 */


  // From ApplicativeError[F, E] or MonadError[F, E] -> EitherT[F, E, A]:
  val try1: EitherT[Try, Throwable, Int] = Try(2).attemptT
  val fut1: EitherT[Future, Throwable, String] = Future.failed(new Exception()).attemptT



  // EitherT[F, A, B] -> F[Either[A, B]]    (extraction):
  val eitherT: EitherT[Future, String, Int] = EitherT.leftT("foo")        // EitherT(Future(Success(Left(foo))))
  val either: Future[Either[String, Int]] = eitherT.value                 // Future(Success(Left(foo)))

}
