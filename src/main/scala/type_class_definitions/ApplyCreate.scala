package type_class_definitions

import FunctorCreate.Functor
import SemigroupalCreate.Semigroupal

object ApplyCreate {

  trait Apply[F[_]] extends Functor[F] with Semigroupal[F] {
    def ap[A, B](fab: F[A => B], fa: F[A]): F[B]
    // Invokes/applies a wrapped function on a wrapped value and return a wrapped result

    
    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] = {              // not standard
      val myFunction: A => B => (A, B) = (a: A) => (b: B) => (a, b)
      val fab: F[B => (A, B)] = map(fa)(myFunction)
      ap(fab, fb)
    }

    def mapN[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {   // not standard
      map(product(fa, fb)) { case (a,b) => f(a,b) }
    }

  }
  

}
