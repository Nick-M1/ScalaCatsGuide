package type_classes.ApplicativeError.create

import type_classes.Applicative.create.ApplicativeCreate.Applicative

object ApplicativeErrorAPI {

  trait ApplicativeError[F[_], E] extends Applicative[F] {    // F is wrapper, E is error type
    def raiseError[A](e: E): F[A]

    def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]

    def handleError[A](fa: F[A])(f: E => A): F[A]

    def attempt[A](fa: F[A]): F[Either[E, A]]
  }

}
