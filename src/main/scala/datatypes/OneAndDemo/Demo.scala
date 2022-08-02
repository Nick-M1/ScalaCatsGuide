package datatypes.OneAndDemo

/* The OneAnd[F[_],A] data type represents a single element of type A that is guaranteed to be present (head) and
*    in addition to this a second part that is wrapped inside an higher kinded type constructor F[_].
*
*  By choosing the F parameter, you can model for example non-empty lists by choosing List for F (shown below)
*    This used to be the implementation of non-empty lists in Cats but has been replaced by the cats.data.NonEmptyList data type.
*  By having the higher kinded type parameter F[_], OneAnd is also able to represent other "non-empty" data structures.
*  */

import cats.data.OneAnd

object Demo {

  type NonEmptyList[A] = OneAnd[List, A]

  type NonEmptyVector[A] = OneAnd[Vector, A]

  /* OneAnd[ F[_], A ] -> 1 element of type A must be present
  *                    -> Could also contain the F[_] type (may or may not) */
}
