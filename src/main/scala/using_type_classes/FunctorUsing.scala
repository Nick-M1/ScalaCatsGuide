package using_type_classes

import cats.Functor
import cats.implicits.*
import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global

object FunctorUsing {

  val listOption: List[Option[Int]] = List(Some(1), None, Some(2))
  // listOption: List[Option[Int]] = List(Some(1), None, Some(2))

  // Through Functor#compose
  Functor[List].compose[Option].map(listOption)(_ + 1)
  // res1: List[Option[Int]] = List(Some(2), None, Some(3))
}
