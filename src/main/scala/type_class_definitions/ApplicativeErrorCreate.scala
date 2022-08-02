package type_class_definitions

import ApplicativeCreate.Applicative

/* Applicatives wrap successful values of type A into a wrapped type F[A],
    ApplicativeError wrap error types and treat them in the same way

    The raiseError method can take an undesirable, “error” value and wrap that into a wrapped type F[A].
    The error type E does not appear in the result type F[A] — that’s because we treat wrapped types in
      the same way down the line, regardless of whether they’re successful or not, and treat the error cases
      later in a purely functional way if we need to.
    */


object ApplicativeErrorCreate {

  trait ApplicativeError[F[_], E] extends Applicative[F] {
    def raiseError[A](e: E): F[A]
    def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
    def handleError[A](fa: F[A])(f: E => A): F[A]
    def attempt[A](fa: F[A]): F[Either[E, A]]
    //More functions elided
  }

}
