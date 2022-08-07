package type_classes.Semigroup.implementations

import simulacrum.typeclass
import cats.Semigroup

object SemigroupImplementations extends App {
  
  // User-defined class (just for demo)
  class ShoppingCart(val items: List[String])

  
  // Put implicits / givens in an object to easily import
  object SemigroupInstances { 
    
    // Implementation of Semigroup for Int:
    given intSemigroup: Semigroup[Int] with
      override def combine(a: Int, b: Int): Int = a + b

    // Implementation of Semigroup for String
    given stringSemigroup: Semigroup[String] with
      override def combine(a: String, b: String): String = a + b

    // Implementation of Semigroup for ShoppingCart
    given cartSemigroup: Semigroup[ShoppingCart] with
      override def combine(a: ShoppingCart, b: ShoppingCart): ShoppingCart = new ShoppingCart(a.items ++ b.items)
  }

  


  // Companion object for easier syntax - Cats has this built-in
  object Semigroup {
    def apply[T](using instance: Semigroup[T]): Semigroup[T] = instance
  }


  // GOTO SemigroupUsing.scala for tests


  // Want to collapse a List of A into a single element (e.g. List[Int] -> sum of List, List[String] -> concatenation)
  def reduceThings1[T](list: List[T])(using semigroup: Semigroup[T]): T = // Given/using method (implicits in Scala 2)
    list.reduce(semigroup.combine)

  def reduceThings2[T: Semigroup](list: List[T]): T = // Context bound method
    list.reduce(Semigroup[T].combine)


  // Testing
  reduceThings1(List(1, 2, 3)) // 6
  reduceThings1(List("i", "love", "scala")) // "ilovescala"


  // EXTENSIONS:
  object SemigroupSyntax { // Given/Using method
    extension[T] (a: T)
      def |+|(b: T)(using semigroup: Semigroup[T]): T = semigroup.combine(a, b)
  }

  //  object SemigroupSyntax {                              // Context-Bound method
  //    extension [T: Semigroup](a: T)
  //      def |+|(b: T): T = Semigroup[T].combine(a, b)
  //  }

  import SemigroupSyntax.|+|

  def reduceThings3[T](list: List[T])(using semigroup: Semigroup[T]): T = // Given/Using method
    list.reduce(_ |+| _)

  def reduceThings4[T: Semigroup](list: List[T]): T = // Context-Bound method
    list.reduce(_ |+| _)


  // Testing:
  reduceThings4((1 to 1000).toList)
  reduceThings4(List("i", "love", "scala"))

}
