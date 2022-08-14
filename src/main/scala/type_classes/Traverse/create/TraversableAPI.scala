package type_classes.Traverse.create

import cats.Applicative

object TraversableAPI {

  trait Traverse[F[_]] {
    def traverse[G[_] : Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
  }



}
