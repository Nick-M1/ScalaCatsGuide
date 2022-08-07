package type_classes.Monoid.implementations

import cats.{Monoid, Semigroup}

object MonoidImplementations {


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

  // List implementation of Monoid - (need to give it a name to use generic type A)
  given ListMonoid[A]: Monoid[List[A]] with {
    def combine(a: List[A], b: List[A]): List[A] = a ++ b
    def empty: List[A] = List()
  }


  // Boolean implementation of Monoid
  given BooleanAndMonoid: Monoid[Boolean] with {              // combine with && 'and' operator
    def combine(a: Boolean, b: Boolean): Boolean = a && b
    def empty = true
  }

  given BooleanOrMonoid: Monoid[Boolean] with {               // combine with || 'or' operator
    def combine(a: Boolean, b: Boolean): Boolean = a || b
    def empty = false
  }


  // Set implementation of Monoid
  given SetUnionMonoid[A]: Monoid[Set[A]] with {              // combine with set union
    def combine(a: Set[A], b: Set[A]): Set[A] = a.union(b)
    def empty = Set.empty[A]
  }

  given SetIntersectionSemigroup[A]: Semigroup[Set[A]] with { // combine with set intersection => however, doesn't have an identity element so can only be a Semigroup
    def combine(a: Set[A], b: Set[A]): Set[A] = a.intersect(b)
  }



}
