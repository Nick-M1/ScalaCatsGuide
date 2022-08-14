package type_classes.Monad.implementations

import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.{Id, Monad}

import scala.annotation.tailrec

object MonadImplementations extends App {
  
  // Implementation of Monad for option
  given Monad[Option] with {
    def pure[A](opt: A): Option[A] = // returns an instance of optionMonad from an unwrapped value
      Some(opt)

    def flatMap[A, B](opt: Option[A])(fn: A => Option[B]): Option[B] =
      opt.flatMap(fn)

    @tailrec // Used for optimising recursion functions
    def tailRecM[A, B](a: A)(fn: A => Option[Either[A, B]]): Option[B] =
      fn(a) match {
        case None => None
        case Some(Left(a1)) => tailRecM(a1)(fn)
        case Some(Right(b)) => Some(b)
      }
  }


  // ------------------------------------------------------------------------
  // TREE DATA-STRUCTURE:
  sealed trait Tree[+A]

  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  final case class Leaf[A](value: A) extends Tree[A]

  def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
    Branch(left, right)

  def leaf[A](value: A): Tree[A] =
    Leaf(value)


  // Tree implementation of Monad
  given Monad[Tree] with {
    def pure[A](value: A): Tree[A] =
      Leaf(value)

    def flatMap[A, B](tree: Tree[A])(func: A => Tree[B]): Tree[B] = tree match {
      case Branch(l, r) => Branch(flatMap(l)(func), flatMap(r)(func))
      case Leaf(value) => func(value)
    }

    def tailRecM[A, B](arg: A)(func: A => Tree[Either[A, B]]): Tree[B] = {
      @tailrec
      def loop(open: List[Tree[Either[A, B]]], closed: List[Option[Tree[B]]]): List[Tree[B]] = open match {
        case Branch(l, r) :: next => loop(l :: r :: next, None :: closed)
        case Leaf(Left(value)) :: next => loop(func(value) :: next, closed)
        case Leaf(Right(value)) :: next => loop(next, Some(pure(value)) :: closed)

        case Nil =>
          closed.foldLeft(Nil: List[Tree[B]]) { (acc, maybeTree) =>
            maybeTree.map(_ :: acc).getOrElse {
              val left :: right :: tail = acc
              branch(left, right) :: tail
            }
          }
      }

      loop(List(func(arg)), Nil).head
    }
  }


  // TEST:
  branch(leaf(100), leaf(200)).
    flatMap(x => branch(leaf(x - 1), leaf(x + 1)))
  // res: Tree[Int] = Branch(
  //      Branch(Leaf(99), Leaf(101)),
  //      Branch(Leaf(199), Leaf(201))
  //      )


  for {
    a <- branch(leaf(100), leaf(200))
    b <- branch(leaf(a - 10), leaf(a + 10))
    c <- branch(leaf(b - 1), leaf(b + 1))
  } yield c
  // re6: Tree[Int] = Branch(
  //    Branch(
  //        Branch(Leaf(89), Leaf(91)),
  //        Branch(Leaf(109), Leaf(111))
  //        ),
  //    Branch(
  //        Branch(Leaf(189), Leaf(191)),
  //        Branch(Leaf(209), Leaf(211))
  //        )
  //    )

  // ----------------------------------------------------------------

  println(List(

    Option(Option(1)).flatten /* Option[Int] = Some(1)        */ ,
    Option(None).flatten /* Option[Nothing] = None       */ ,
    List(List(1), List(2, 3)).flatten /* List[Int] = List(1, 2, 3)    */

  ))


}
