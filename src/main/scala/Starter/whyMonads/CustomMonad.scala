package Starter.whyMonads

import simulacrum.typeclass
import scala.annotation.implicitNotFound

object CustomMonad extends App {

  // Custom Monad type-class (this is built-in to Cats)
  @implicitNotFound("Could not find an instance of Monad for ${M}") // When Intellij highlights implicits error, this msg appears
  @typeclass
  trait Monad[M[_]] {
    def pure[A](a: A): M[A]

    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

    def tailRecM[A, B](a: A)(fn: A => M[Either[A, B]]): M[B]

    def map[A, B](ma: M[A])(f: A => B): M[B] = // Implemented for free (don't need to define for an implementation, as it uses other methods that are already defined)
      flatMap(ma)(a => pure(f(a)))
  }


  // List implementation of Monad (uses the built-in List methods)
  given Monad[List] with {
    override def pure[A](a: A): List[A] = List(a)

    override def flatMap[A, B](ma: List[A])(f: A => List[B]): List[B] = ma.flatMap(f)

    override def tailRecM[A, B](a: A)(fn: A => List[Either[A, B]]): List[B] = ??? // Don't need for now
  }

  // Option implementation of Monad
  given Monad[Option] with {
    override def pure[A](opt: A): Option[A] = Some(opt)

    override def flatMap[A, B](opt: Option[A])(fn: A => Option[B]): Option[B] = opt.flatMap(fn)

    override def tailRecM[A, B](a: A)(fn: A => Option[Either[A, B]]): Option[B] = ???
  }


  // Function to 'combine' 2 wrappers (1 wrapper of Strings & 1 wrapper of Int)
  def combine_v1[M[_]](str: M[String])(num: M[Int])(using monad: Monad[M]): M[(String, Int)] =
    monad.flatMap(str)(s => monad.map(num)(n => (s, n)))


  // Test
  combine_v1(List("a", "b", "c"))(List(1, 2, 3)) // List( (a,1), (a,2), (a,3), (b,1), (b,2), (b,3), (c,1), (c,2), (c,3) )
  combine_v1(Option("Hello"))(Option(6)) // Some((Hello,6))



  // Extension methods --> For-comprehensions
  extension[M[_], A] (ma: M[A])(using monad: Monad[M]) {
    def map[B](f: A => B): M[B] = monad.map(ma)(f)
    def flatMap[B](f: A => M[B]): M[B] = monad.flatMap(ma)(f)
  }

  def combine_v2[M[_] : Monad](str: M[String])(num: M[Int]): M[(String, Int)] = for {
    s <- str
    n <- num
  } yield (s, n)

  // combine_v1 == combine_v2


}
