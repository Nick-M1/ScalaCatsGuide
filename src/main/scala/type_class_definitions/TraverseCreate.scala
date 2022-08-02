package type_class_definitions

import cats.Applicative

object TraverseCreate {

  trait Traverse[F[_]] {
    def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
  }

  // List implementation of Traverse
  given Traverse[List] with {
    def traverse[G[_]: Applicative, A, B](fa: List[A])(f: A => G[B]): G[List[B]] =
      fa.foldRight(Applicative[G].pure(List.empty[B])) { (a: A, acc: G[List[B]]) =>
        Applicative[G].map2(f(a), acc)(_ :: _)
      }
  }


  // TREE-DATASTRUCTURE:
  sealed abstract class Tree[A] extends Product with Serializable {
    def traverse[F[_]: Applicative, B](f: A => F[B]): F[Tree[B]] = this match {
      case Tree.Empty()         => Applicative[F].pure(Tree.Empty())
      case Tree.Branch(v, l, r) => Applicative[F].map3(f(v), l.traverse(f), r.traverse(f))(Tree.Branch(_, _, _))
    }
  }

  object Tree {
    final case class Empty[A]() extends Tree[A]
    final case class Branch[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]
  }

  // Tree implementation of Traverse - uses Tree
  given Traverse[Tree] with {
    def traverse[G[_]: Applicative, A, B](fa: Tree[A])(f: A => G[B]): G[Tree[B]] =
      fa.traverse(f)
  }


}
