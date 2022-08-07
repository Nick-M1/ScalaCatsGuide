package type_classes.Applicative.create

import simulacrum.typeclass
import type_classes.Apply.create.ApplyAPI.Apply

object ApplicativeCreate {

  @typeclass
  trait Applicative[F[_]] extends Apply[F] {
    def pure[A](a: A): F[A] // Lifts a plain value to a Monadic type (e.g. Int -> Option[Int])

    def map[A, B](fa: F[A])(f: A => B): F[B] =
      ap(pure(f), fa)
  }


}
