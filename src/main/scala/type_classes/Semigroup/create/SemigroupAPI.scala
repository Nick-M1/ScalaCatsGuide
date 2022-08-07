package type_classes.Semigroup.create

import simulacrum.typeclass

object SemigroupAPI extends App {

  // Type-class (abstract & generic)
  @typeclass
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

}
