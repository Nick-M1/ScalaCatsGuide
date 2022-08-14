package type_classes.Apply.create

import cats.{Functor, Semigroupal}


object ApplyAPI {

  trait Apply[F[_]] extends Functor[F] with Semigroupal[F] {
    
    // Methods that need to be implemented:
    def ap[A, B](fab: F[A => B], fa: F[A]): F[B]    // Invokes/applies a wrapped function on a wrapped value and return a wrapped result


    // Methods implemented for free - their implementation uses other methods that are already defined
    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] = {
      val myFunction: A => B => (A, B) = (a: A) => (b: B) => (a, b)
      val fab: F[B => (A, B)] = map(fa)(myFunction)
      ap(fab, fb)
    }

    def mapN[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
      map(product(fa, fb)) { case (a, b) => f(a, b) }
    }

  }


}
