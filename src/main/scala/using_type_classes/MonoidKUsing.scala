package using_type_classes

object MonoidKUsing extends App {

  import cats.{Monoid, MonoidK}
  import cats.implicits._

  Monoid[List[String]].empty    // List[String] = List()
  MonoidK[List].empty[String]   // List[String] = List()
  MonoidK[List].empty[Int]      // List[Int] = List()


  Monoid[List[String]].combine(List("hello", "world"), List("goodbye", "moon"))
    // List[String] = List("hello", "world", "goodbye", "moon")
  MonoidK[List].combineK[String](List("hello", "world"), List("goodbye", "moon"))
    // List[String] = List("hello", "world", "goodbye", "moon")
  MonoidK[List].combineK[Int](List(1, 2), List(3, 4))
    // List[Int] = List(1, 2, 3, 4)

  // The type parameter can be inferred
  MonoidK[List].combineK(List("hello", "world"), List("goodbye", "moon"))
    // List[String] = List("hello", "world", "goodbye", "moon")
  MonoidK[List].combineK(List(1, 2), List(3, 4))
    // List[Int] = List(1, 2, 3, 4)

}
