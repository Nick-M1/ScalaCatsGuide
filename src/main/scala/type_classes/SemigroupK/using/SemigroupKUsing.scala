package type_classes.SemigroupK.using

import cats._
import cats.implicits._

/** When using SemigroupK, the type thats inside the 'wrapper' type is unknown, unlike when using Semigroup <p>
 *  When using Option or List, then with SemigroupK the compiler doesn't know what is in the Option or List. <br>
 *  But, when using Semigroup, the compiler would know the inner type in Option[A] & List[A]
 * */
object SemigroupKUsing extends App {



  // Using combine on List is the same for Semigroup & SemigroupK - concatenates the lists together:
  Semigroup[List[Int]].combine(List(1, 2, 3), List(4, 5, 6))      // List(1, 2, 3, 4, 5, 6)
  SemigroupK[List].combineK(List(1, 2, 3), List(4, 5, 6))         // List(1, 2, 3, 4, 5, 6)
  

  // However, combine on Options is different for Semigroup & SemigroupK:
  // * Semigroup will combine the values inside the options (as it knows their type) - With Option[Int], it will add the Ints together
  // * SemigroupK just tries to return the 1st option in combineK's args that is a Some (as it doesn't know the type inside the Option, so doesn't know how to combine them)
  Semigroup[Option[Int]].combine(Some(1), Some(2))  // Some(3)
  
  SemigroupK[Option].combineK(Some(1), Some(2))     // Some(1)  -> returns the 1st Some(...) in the args
  SemigroupK[Option].combineK(Some(1), None)        // Some(1)
  SemigroupK[Option].combineK(None, Some(2))        // Some(2)
  SemigroupK[Option].combineK(None, None)           // None     -> if no Some found, returns None
  
  
  // Easier syntax for Semigroup & SemigroupK (from Cats)
  // |+| = Semigroup.combine,   <+> = SemigroupK.combineK
  val one: Option[Int] = Option(1)
  val two: Option[Int] = Option(2)
  val n: Option[Int] = None

  one |+| two   // Some(3)  -> Semigroup combines the ints together
  one <+> two   // Some(1)  -> SemigroupK just returns the 1st Some it finds

  n |+| two     // Some(2)
  n <+> two     // Some(2)

  n |+| n       // None
  n <+> n       // None

  two |+| n     // Some(2)
  two <+> n     // Some(2)

}
