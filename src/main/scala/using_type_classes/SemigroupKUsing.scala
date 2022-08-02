package using_type_classes



object SemigroupKUsing extends App {


  import cats._
  import cats.implicits._

  // These are the same:
  SemigroupK[List].combineK(List(1,2,3), List(4,5,6))
  Semigroup[List[Int]].combine(List(1,2,3), List(4,5,6))
      // List(1, 2, 3, 4, 5, 6)


  Semigroup[Option[Int]].combine(Some(1), Some(2))    // Some(3)
  SemigroupK[Option].combineK(Some(1), Some(2))       // Some(1)
  SemigroupK[Option].combineK(Some(1), None)          // Some(1)
  SemigroupK[Option].combineK(None, Some(2))          // Some(2)



  import cats.implicits._

  val one: Option[Int] = Option(1)
  val two: Option[Int] = Option(2)
  val n: Option[Int] = None

  one |+| two     // Some(3)
  one <+> two     // Some(1)

  n |+| two       // Some(2)
  n <+> two       // Some(2)

  n |+| n         // None
  n <+> n         // None

  two |+| n       // Some(2)
  two <+> n       // Some(2)

}
