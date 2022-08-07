package type_classes.Semigroupal.create

object SemigroupalCreate {

  trait Semigroupal[F[_]] {
    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
    // Given 2 wrapped values, returns  a wrapped value of tuple(s)
    // E.g. If given 2 Lists, returns the cartesian product of the 2 lists as a single list
  }

}
