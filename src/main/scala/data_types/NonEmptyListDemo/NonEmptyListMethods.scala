package data_types.NonEmptyListDemo

import cats.data.NonEmptyList

object NonEmptyListMethods {

  // "MethodName()" -> "Its arguments:"



  // one() <- 1 element:
  NonEmptyList.one(42)          // NonEmptyList[Int] = NonEmptyList(42, List())

  // of() <- Vargs with multiple elements:
  NonEmptyList.of(1)                   // NonEmptyList[Int] = NonEmptyList(1, List())
  NonEmptyList.of(1, 2)           // NonEmptyList[Int] = NonEmptyList(1, List(2))
  NonEmptyList.of(1, 2, 3, 4)     // NonEmptyList[Int] = NonEmptyList(1, List(2, 3, 4))

  // ofInitLast() <- List & what the last element in NonEmptyList will be
  NonEmptyList.ofInitLast(List(), 4)          // NonEmptyList[Int] = NonEmptyList(4, List())
  NonEmptyList.ofInitLast(List(1,2,3), 4)     // NonEmptyList[Int] = NonEmptyList(1, List(2, 3, 4))


  // fromList <- List     (returns Option[NonEmptyList):
  NonEmptyList.fromList(List())           // Option[NonEmptyList[Nothing]] = None
  NonEmptyList.fromList(List(1,2,3))      // Option[NonEmptyList[Int]] = Some(NonEmptyList(1, List(2, 3)))

  import cats.syntax.list._   // Simpler syntax
  List(1,2,3).toNel                       // Option[NonEmptyList[Int]] = Some(NonEmptyList(1, List(2, 3)))



  // fromFoldable() & fromReducible() <- Datastructures that have Foldable or Reducible traits
  /* Note: fromReducible doesn't have Option in the return type, because it is only available for non-empty datastructures. */
  import cats.implicits._

  NonEmptyList.fromFoldable(List())                                   // Option[NonEmptyList[Nothing]] = None
  NonEmptyList.fromFoldable(List(1,2,3))                              // Option[NonEmptyList[Int]] = Some(NonEmptyList(1, List(2, 3)))

  NonEmptyList.fromFoldable(Vector(42))                               // Option[NonEmptyList[Int]] = Some(NonEmptyList(42, List()))
  NonEmptyList.fromFoldable(Vector(42))                               // Option[NonEmptyList[Int]] = Some(NonEmptyList(42, List()))

  NonEmptyList.fromFoldable(Either.left[String, Int]("Error"))        // Option[NonEmptyList[Int]] = None
  NonEmptyList.fromFoldable(Either.right[String, Int](42))            // Option[NonEmptyList[Int]] = Some(NonEmptyList(42, List()))


  import cats.data.NonEmptyVector
  NonEmptyList.fromReducible(NonEmptyVector.of(1, 2, 3))   // NonEmptyList[Int] = NonEmptyList(1, List(2, 3))

}
