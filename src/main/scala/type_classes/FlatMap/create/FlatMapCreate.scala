package type_classes.FlatMap.create

import cats.Functor

object FlatMapCreate {

  trait FlatMap[F[_]] extends Functor[F] {
    def flatMap[A, B](fa: F[A])(f: A => B): F[B]        // “chain” computations/transformations of monadic types
  }


}
