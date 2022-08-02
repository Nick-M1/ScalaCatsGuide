package datatypes.IdDemo

/* The Identity Monad:
*  Ambient Monad that encodes the effect of having no effect (ambient as it just wraps plain values, which have no side-effects)
*
*  Treated as a Monad & Comonad
* */

import cats.implicits._
import cats.{Functor, Monad, Comonad, Id}

object Demo1 {

  // Encoded as:
  type myId[A] = A        // Id[A] is just an alias for A. So types of A are also types of Id[A] & vice versa


  val x: Id[Int] = 1      // x: Id[Int] = 1
  val y: Int = x          // y: Int = 1


  Functor[Id].map(x)(_ + 1)     // Id[Int] = 2
  Functor[Id].map(y)(_ + 1)     // Id[Int] = 2

  Monad[Id].map(y)(_ + 1)       // Id[Int] = 2
  Monad[Id].flatMap(y)(_ + 1)   // Id[Int] = 2

  Comonad[Id].coflatMap(y)(_ + 1)   // Id[Int] = 2

}
