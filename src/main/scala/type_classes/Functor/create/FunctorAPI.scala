package type_classes.Functor.create

object FunctorAPI {

  trait Functor[F[_]] {
    // Methods that need to be implemented:
    def map[A, B](container: F[A])(f: A => B): F[B]
  }


  // Extension method - for easier syntax (already included in Cats)
  extension [F[_], A](container: F[A])(using functor: Functor[F]) {
    def map[B](f: A => B): F[B] = functor.map(container)(f)
  }
  
  val _ = List(1, 2, 3).map(_.toString)     // Allows for this syntax, but for implementations of Functor     

}
