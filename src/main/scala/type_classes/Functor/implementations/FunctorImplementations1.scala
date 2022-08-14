package type_classes.Functor.implementations

import cats.Functor

import scala.annotation.tailrec

object FunctorImplementations1 extends App {


  // Example implementation for Option - Given in Cats
  given Functor[Option] with
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None => None
      case Some(a) => Some(f(a))
    }


  // Example implementation for List - Given in Cats
  given Functor[List] with
    def map[A, B](list: List[A])(function: A => B): List[B] = {

      @tailrec
      def loop(rem: List[A], acc: List[B]): List[B] = rem match { // rem = remaining list
        case Nil => acc.reverse
        case head :: tail => loop(tail, function(head) :: acc)
      }

      loop(list, Nil)
    }


  // Function using this Functor type-class
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)


  do10x(List(1, 2, 3)) // List(10, 20, 30)



}
