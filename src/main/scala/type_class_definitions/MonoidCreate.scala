package type_class_definitions

import SemigroupCreate.Semigroup

object MonoidCreate {

  /* Monoid inherits all the properties & methods of Semigroup, plus the empty method */
  trait Monoid[T] extends Semigroup[T] {
    def empty: T
  }

  // Integer implementation of Monoid
  given Monoid[Int] with {
    def combine(a: Int, b: Int): Int = a + b
    def empty: Int = 0
  }

  // String implementation of Monoid
  given Monoid[String] with {
    def combine(a: String, b: String): String = a + b
    def empty: String = ""
  }

  // List implementation of Monoid
  // (need to give it a name to use generic type A)
  given ListMonoid[A]: Monoid[List[A]] with {
    def combine(a: List[A], b: List[A]): List[A] = a ++ b
    def empty: List[A] = List()
  }




}
