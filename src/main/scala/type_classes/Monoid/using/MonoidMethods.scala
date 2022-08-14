package type_classes.Monoid.using

import cats.Monoid
import cats.implicits._

object MonoidMethods {
  
  /** isEmpty: <p>
   * Tests if a value is the identity.
   * */
  Monoid[String].isEmpty("")            // res: Boolean = true
  Monoid[String].isEmpty("something")   // res: Boolean = false
  
  
  /** combineN: <p>
   *  Return the value appended to itself n times
   * */
  Monoid[String].combineN("ha", 3)      // res: String = hahaha
  Monoid[String].combineN("ha", 0)      // res: String = ""
  
  /** combineAll: <br>
   * Given an iterable, sum them using the monoid and return the total
   * 
   * combineAllOption: <br>
   * Same as combineAll, but if the iterable is empty then returns None instead of the Monad#empty
   * */
  Monoid[String].combineAll(List("One ", "Two ", "Three"))        // res: String = "One Two Three"
  Monoid[String].combineAll(List.empty)                           // res: String = ""

  Monoid[String].combineAllOption(List("One ", "Two ", "Three"))  // res: Option[String] = Some("One Two Three")
  Monoid[String].combineAllOption(List.empty)                     // res: Option[String] = None

}
