package type_classes.Monoid.create

import cats.Semigroup

object MonoidCreate {

  /* Monoid inherits all the properties & methods of Semigroup, plus the empty method */
  trait Monoid[T] extends Semigroup[T] {
    def combine: T        // Combines 2 monoids (of the same type) together and returns result
    def empty: T          // Returns the identity element for this monoid
  }


}
