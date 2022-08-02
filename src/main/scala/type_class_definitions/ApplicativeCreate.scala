package type_class_definitions

import ApplyCreate.Apply
import simulacrum.typeclass

object ApplicativeCreate {

  @typeclass
  trait Applicative[F[_]] extends Apply[F] {
    def pure[A](a: A): F[A]                       // Lifts a plain value to a Monadic type (e.g. Int -> Option[Int])

    def map[A, B](fa: F[A])(f: A => B): F[B] =
      ap(pure(f), fa)
  }



}
