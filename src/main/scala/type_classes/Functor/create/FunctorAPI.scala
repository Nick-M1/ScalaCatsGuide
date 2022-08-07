package type_classes.Functor.create

object FunctorAPI {

  trait Functor[F[_]] {
    // Methods that need to be implemented:
    def map[A, B](container: F[A])(f: A => B): F[B]

    
    // Methods implemented for free - their implementation uses other methods that are already defined
    def lift[A, B](f: A => B): F[A] => F[B] = fa => map(fa)(f)

    def widen[A, B >: A](fa: F[A]): F[B] = fa.asInstanceOf[F[B]]

    def void[A](fa: F[A]): F[Unit] = as(fa, ())

    def fproduct[A, B](fa: F[A])(f: A => B): F[(A, B)] = map(fa)(a => a -> f(a))
    def fproductLeft[A, B](fa: F[A])(f: A => B): F[(B, A)] = map(fa)(a => f(a) -> a)

    def as[A, B](fa: F[A], b: B): F[B] = map(fa)(_ => b)

    def tupleLeft[A, B](fa: F[A], b: B): F[(B, A)] = map(fa)(a => (b, a))
    def tupleRight[A, B](fa: F[A], b: B): F[(A, B)] = map(fa)(a => (a, b))

    def unzip[A, B](fab: F[(A, B)]): (F[A], F[B]) = (map(fab)(_._1), map(fab)(_._2))
    def ifF[A](fb: F[Boolean])(ifTrue: => A, ifFalse: => A): F[A] = map(fb)(x => if (x) ifTrue else ifFalse)

  }


  // Extension method - for easier syntax (already included in Cats)
  extension [F[_], A](container: F[A])(using functor: Functor[F]) {
    def map[B](f: A => B): F[B] = functor.map(container)(f)
  }
  
  val _ = List(1, 2, 3).map(_.toString)     // Allows for this syntax, but for implementations of Functor     

}
