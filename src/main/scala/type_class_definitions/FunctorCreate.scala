package type_class_definitions

import scala.annotation.tailrec

import cats.Functor

object FunctorCreate extends App {

  trait Functor[F[_]] {
    def map[A, B](container: F[A])(f: A => B): F[B]

//    def lift[A, B](f: A => B): F[A] => F[B] =     // Just for demo, not actually implemented
//      fa => map(fa)(f)
  }


  // Example implementation for Option
  given Functor[Option] with                                                      // Scala 3
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None    => None
      case Some(a) => Some(f(a))
    }

//  implicit val functorForOption2: Functor[Option] = new Functor[Option] {       // Scala 2
//    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
//      case None    => None
//      case Some(a) => Some(f(a))
//    }
//  }



  // Example implementation for List
  given Functor[List] with                                           // Scala 3
    def map[A, B](list: List[A])(function: A => B): List[B] = {

      @tailrec
      def loop(rem: List[A], acc: List[B]): List[B] = rem match {     // rem = remaining list
        case Nil => acc.reverse
        case head :: tail => loop(tail, function(head) :: acc)
      }
      loop(list, Nil)
    }

//  implicit val functorForList: Functor[List] = new Functor[List] {
//    def map[A, B](list: List[A])(function: A => B): List[B] = {
//
//      @tailrec
//      def loop(rem: List[A], acc: List[B]): List[B] = rem match {     // rem = remaining list
//        case Nil => acc.reverse
//        case head :: tail => loop(tail, function(head) :: acc)
//      }
//      loop(list, Nil)
//    }
//  }

//  val ListSemigroup: Functor[List[Int]] = implicitly(Functor[List[Int]])
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)


  do10x(List(1,2,3))          // List(10, 20, 30)




  // #2 - TREE:

  // Basic Tree Data-structure
  trait Tree[+T]

  object Tree {
    def leaf[T](value: T): Tree[T] = Leaf(value)
    def branch[T](value: T, left: Tree[T], right: Tree[T]): Tree[T] = Branch(value, left, right)
  }

  case class Leaf[+T](value: T) extends Tree[T]
  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]


  // Tree implementation of Functor
  given Functor[Tree] with
    override def map[A, B](container: Tree[A])(func: A => B): Tree[B] = container match
      case Branch(value, left, right) => Branch(func(value), map(left)(func), map(right)(func))   // recursively call map on each child node of the root
      case Leaf(value) => Leaf(func(value))


  // To be able to use Functor's map method in prefix notation ( container.map(func) ), instead of Functor[Tree].map(container)(func)
  extension [C[_], A, B](container: C[A])(using functor: Functor[C])
    def map(func: A => B) = functor.map(container)(func)



  val tree: Tree[Int] =
    Tree.branch(1,
      Tree.branch(2, Tree.leaf(3), Tree.leaf(4)),
      Tree.leaf(5),
    )



  println(tree)                   // Branch(1,Branch(2,Leaf(3),Leaf(4)),Leaf(5))

  println(do10x(tree))            // These are same:   Branch(10,Branch(20,Leaf(30),Leaf(40)),Leaf(50))
  println(tree.map(_ * 10))



}
