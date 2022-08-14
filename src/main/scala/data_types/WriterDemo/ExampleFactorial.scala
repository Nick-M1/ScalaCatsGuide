package data_types.WriterDemo

import cats.data.Writer
import cats.implicits._
import scala.concurrent.{Await, Future}
import concurrent.ExecutionContext.Implicits.global
import concurrent.duration.DurationInt

object ExampleFactorial {

  type Logged[A] = Writer[Vector[String], A]        // type alias, for easier syntax

  def factorial(n: Int): Logged[Int] = for {
    ans <- if n == 0 then 1.pure[Logged] else factorial(n - 1).map(_ * n)
    _ <- Vector(s"fact $n $ans").tell
  } yield ans

  // returns ( Vector(of logs...), resultInt)


  // Run a single thread:
  factorial(5).run
  // result: (Vector[String], Int) = (
  //    Vector("fact 0 1", "fact 1 1", "fact 2 2", "fact 3 6", "fact 4 24", "fact 5 120"),
  //    120
  // )


  // Run 2 threads of factorial at the same time - logging msgs are separated based on their thread (less confusing):
  Await.result(Future.sequence(
    Vector(Future(factorial(5)), Future(factorial(5)))
  ), 5.seconds)
  // res: scala.collection.immutable.Vector[cats.Id[Vector[String]]] =
  // Vector(
  //    (Vector(fact 0 1, fact 1 1, fact 2 2, fact 3 6, fact 4 24, fact 5 120), 120),
  //    (Vector(fact 0 1, fact 1 1, fact 2 2, fact 3 6, fact 4 24, fact 5 120), 120)
  // )
}
