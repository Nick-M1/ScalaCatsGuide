package type_classes.Monad.using

import cats.{Id, Monad}
import cats.syntax.functor.*
import cats.syntax.flatMap.*
import scala.annotation.tailrec

import scala.annotation.tailrec

object MonadUsing extends App {

  // flatMap method:
  // sumSquare1() & sumSquare2() are compiled to the exact same bit of code (so are exactly the same)
  def sumSquare1[F[_] : Monad](a: F[Int], b: F[Int]): F[Int] = for {
    x <- a
    y <- b
  } yield x * y

  def sumSquare2[F[_] : Monad](a: F[Int], b: F[Int]): F[Int] =
    a.flatMap(x => b.map(y => x * x + y * y))


  sumSquare1(Option(3), Option(4)) // Option[Int] = Some(25)
  sumSquare1(List(1, 2, 3), List(4, 5)) // List[Int] = List(17, 26, 20, 29, 25, 34)

  /* However, sumSquare1&2() only works with Monads
     For non-monad types, use the Id Monad to 'wrap' the non-monad type
     type Id[A] = A                 */

  val a: Id[String] = Id("Dave")
  val b: Id[Int] = Id(123)

  sumSquare1(Id(3), Id(4))


  


}
