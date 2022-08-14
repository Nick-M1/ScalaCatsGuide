package Starter.whyMonads

import simulacrum.typeclass
import scala.annotation.implicitNotFound

object CustomMonad extends App {

  /** Custom Monad type-class (this is built-in to Cats) <p>
   *  F[_] is a generic for any type that wraps another type (e.g List, Set, Option, Either). */
  @implicitNotFound("Could not find an instance of Monad for ${F}") // When Intellij highlights implicits error, this msg appears
  @typeclass
  trait Monad[F[_]] {
    def pure[A](a: A): F[A]

    def flatMap[A, B](ma: F[A])(f: A => F[B]): F[B]

    def tailRecM[A, B](a: A)(fn: A => F[Either[A, B]]): F[B]

    def map[A, B](ma: F[A])(f: A => B): F[B] = // Implemented for free (don't need to define for an implementation, as it uses other methods that are already defined)
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
  def combine_v1[F[_]](str: F[String])(num: F[Int])(using monad: Monad[F]): F[(String, Int)] =
    monad.flatMap(str)(s => monad.map(num)(n => (s, n)))


  // Test
  combine_v1(List("a", "b", "c"))(List(1, 2, 3)) // List( (a,1), (a,2), (a,3), (b,1), (b,2), (b,3), (c,1), (c,2), (c,3) )
  combine_v1(Option("Hello"))(Option(6)) // Some((Hello,6))



  // Extension methods --> For-comprehensions
  extension[F[_], A] (ma: F[A])(using monad: Monad[F]) {
    def map[B](f: A => B): F[B] = monad.map(ma)(f)
    def flatMap[B](f: A => F[B]): F[B] = monad.flatMap(ma)(f)
  }

  def combine_v2[F[_] : Monad](str: F[String])(num: F[Int]): F[(String, Int)] = for {
    s <- str
    n <- num
  } yield (s, n)

  // combine_v1 == combine_v2


}
