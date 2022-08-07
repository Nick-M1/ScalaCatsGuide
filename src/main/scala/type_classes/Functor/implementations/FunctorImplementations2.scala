package type_classes.Functor.implementations

import cats.Functor
import type_classes.Functor.implementations.FunctorImplementations1.do10x

import scala.annotation.tailrec

object FunctorImplementations2 extends App {

  // #2 - TREE DATA-STRUCTURE:

  // Basic Tree Data-structure
  trait Tree[+T]

  object Tree {
    def leaf[T](value: T): Tree[T] = Leaf(value)

    def branch[T](value: T, left: Tree[T], right: Tree[T]): Tree[T] = Branch(value, left, right)
  }

  case class Leaf[+T](value: T) extends Tree[T]

  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]


  // Tree implementation of Functor - As Tree is a user-defined class, the Tree implementation of Functor must be defined by user as well
  given Functor[Tree] with
    override def map[A, B](container: Tree[A])(func: A => B): Tree[B] = container match
      case Branch(value, left, right) => Branch(func(value), map(left)(func), map(right)(func)) // recursively call map on each child node of the root
      case Leaf(value) => Leaf(func(value))


  // To be able to use Functor's map method in prefix notation ( container.map(func) ), instead of Functor[Tree].map(container)(func)
  extension[C[_], A, B] (container: C[A])(using functor: Functor[C])
    def map(func: A => B) = functor.map(container)(func)


  val tree: Tree[Int] =
    Tree.branch(1,
      Tree.branch(2, Tree.leaf(3), Tree.leaf(4)),
      Tree.leaf(5),
    )


  println(tree) // Branch(1,Branch(2,Leaf(3),Leaf(4)),Leaf(5))

  println(do10x(tree)) // These are same:   Branch(10,Branch(20,Leaf(30),Leaf(40)),Leaf(50))
  println(tree.map(_ * 10))


}
