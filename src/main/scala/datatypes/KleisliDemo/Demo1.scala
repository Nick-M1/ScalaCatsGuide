package datatypes.KleisliDemo

import cats.Functor
import cats.FlatMap
import cats.data.Kleisli
import cats.implicits._
import cats.data.Kleisli


object Demo1 extends App {

  /* Kleisli[F[_], A, B] is just a wrapper around the function A => F[B].
  *
  *  Depending on the properties of the F[_], we can do different things with Kleislis.
  *  For instance, if F[_] has a FlatMap[F] instance (we can call flatMap on F[A] values),
  *   we can compose two Kleislis much like we can two functions.                            */


  // Modified functions from word.doc, but with Kleisli
  val parse: Kleisli[Option,String,Int] =
    Kleisli((s: String) => if s.matches("-?[0-9]+") then Some(s.toInt) else None)

  val reciprocal: Kleisli[Option,Int,Double] =
    Kleisli((i: Int) => if (i != 0) then Some(1.0 / i)  else None)

  val parseAndReciprocal: Kleisli[Option,String,Double] =
    reciprocal.compose(parse)
  

}
