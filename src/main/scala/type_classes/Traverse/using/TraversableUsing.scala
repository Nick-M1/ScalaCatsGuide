package type_classes.Traverse.using

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import cats.{Applicative, Traverse}
import cats.data.Validated
import cats.syntax._
import cats.implicits._

/** Traverse is a higher‐level abstraction that uses Applicatives to iterate more easily than folding. */
object TraversableUsing extends App {

  val hostnames = List(
    "alpha.example.com",
    "beta.example.com",
    "gamma.demo.com"
  )

  def getUptime(hostname: String): Future[Int] = // any process
    Future(hostname.length * 60)


  val allUptimes: Future[List[Int]] = Future.traverse(hostnames)(getUptime)   // traverse applies/maps (efficiently) each future elem in hostnames[] to getUpTime() func
  Await.result(allUptimes, 1.second)                                          // res: List[Int] = List(1020, 960, 840)


  // Custom List Traversal - traverses list & applies function to each element:
  def listTraverse[F[_] : Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
    list.foldLeft(List.empty[B].pure[F]) { 
      (accum, item) => (accum, func(item)).mapN(_ :+ _)
    }

  // Custom List Sequence - traverses list & applies identity function to each element:
  def listSequence[F[_] : Applicative, B](list: List[F[B]]): F[List[B]] =
    listTraverse(list)(identity)

  val totalUptime = listTraverse(hostnames)(getUptime)
  Await.result(totalUptime, 1.second) // res: List[Int] = List(1020, 960, 840)


  listSequence(List(Vector(1, 2), Vector(3, 4))) // res: Vector[List[Int]] = Vector( List(1, 3), List(1, 4), List(2, 3), List(2, 4) )


  // Traversing then Options
  def processList(inputs: List[Int]) =
    listTraverse(inputs)(n => if (n % 2 == 0) Some(n) else None)

  processList(List(2, 4, 6)) // res: Option[List[Int]] = Some(List(2, 4, 6))     -> all elems even, so all Some(n), so all valid
  processList(List(1, 2, 3)) // res: Option[List[Int]] = None                    -> at least 1 elem not even, so all get returned as a None


  // Traversing then Validated
  type ErrorsOr[A] = Validated[List[String], A]

  def processValidated(inputs: List[Int]): ErrorsOr[List[Int]] =
    listTraverse(inputs)(n => if n % 2 == 0 then Validated.valid(n) else Validated.invalid(List(s"$n is not even")))

  processValidated(List(2, 4, 6)) // res: ErrorsOr[List[Int]] = Valid(List(2, 4, 6))
  processValidated(List(1, 2, 3)) // res: ErrorsOr[List[Int]] = Invalid(List("1 is not even", "3 is not even"))


  // Traverse with CATS (better syntax):
  //  val totalUpTime: Future[List[Int]] = Traverse[List].traverse(hostnames)(getUptime)
  //  Await.result(totalUpTime, 1.second)                 // res: List[Int] = List(1020, 960, 840)
  //
  val numbers = List(Future(1), Future(2), Future(3))
  //  val numbers2: Future[List[Int]] = Traverse[List].sequence(numbers)
  //  Await.result(numbers2, 1.second)                    // res: List[Int] = List(1, 2, 3)

  Await.result(hostnames.traverse(getUptime), 1.second) // res: List[Int] = List(1020, 960, 840)
  Await.result(numbers.sequence, 1.second) // res: List[Int] = List(1, 2, 3)


}
