package type_classes.Semigroupal.using

import cats.Semigroupal
import cats.implicits._

object SemigroupalUsing {
  
  /** product: <p>
   * Combine an F[A] and an F[B] into an F[(A, B)] that maintains the effects of both fa and fb.
   * */
  val noneInt: Option[Int] = None
  val someInt: Option[Int] = Some(3)
  val noneString: Option[String] = None
  val someString: Option[String] = Some("foo")

  Semigroupal[Option].product(noneInt, noneString)      // res: Option[(Int, String)] = None  -> if either or both options are None, returns None
  Semigroupal[Option].product(noneInt, someString)      // res: Option[(Int, String)] = None
  Semigroupal[Option].product(someInt, noneString)      // res: Option[(Int, String)] = None
  Semigroupal[Option].product(someInt, someString)      // res: Option[(Int, String)] = Some((3, foo)) -> Bot options must be Some, then returns Some(tuple)

}
