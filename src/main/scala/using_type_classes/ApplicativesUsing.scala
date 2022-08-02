package using_type_classes

import cats.Applicative           // trait Applicative


object ApplicativesUsing extends App {

  // Pure method: Wraps a value
  // E.g.  Int -> List[Int],   String -> Option[String]

  import cats.instances.list._    // implementation of List's Applicative
  Applicative[List].pure(2)       // List(2)

  import cats.instances.option._  // implementation of Option's Applicative
  Applicative[Option].pure(2)     // Some(2)


  // Pure extension method
  import cats.syntax.applicative._
  2.pure[List]                    // List(2)
  2.pure[Option]                  // Some(2)



  import cats.implicits._

  println(
  Applicative[Option].map3(Some(5), Some(6), Some(7))(_ + _ + _)
  )
//  Applicative[Option].map3(Some(5), None, Some(7))(_ + _ + _)


}
