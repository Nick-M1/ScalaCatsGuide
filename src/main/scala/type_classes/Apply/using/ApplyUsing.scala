package type_classes.Apply.using

import cats.Apply
import cats.data.Validated
import cats.data.Validated.{Valid, Invalid}

object ApplyUsing {

  /** ap: <p>
   * Given a value and a function in the Apply context, applies the function to the value. */

  val someF: Option[Int => Long] = Some(_.toLong + 1L)
  val noneF: Option[Int => Long] = None
  val someInt: Option[Int] = Some(3)
  val noneInt: Option[Int] = None

  Apply[Option].ap(someF)(someInt)            // res: Option[Long] = Some(4)
  Apply[Option].ap(noneF)(someInt)            // res: Option[Long] = None

  Apply[Option].ap(someF)(noneInt)            // res: Option[Long] = None
  Apply[Option].ap(noneF)(noneInt)            // res: Option[Long] = None


  /** productL: <br>
   *  Compose two actions, discarding any value produced by the first. <p>
   *
   *  productR: <br>
   *  Compose two actions, discarding any value produced by the second.
   * */

  type ErrOr[A] = Validated[String, A]

  val validInt: ErrOr[Int] = Valid(3)
  val validBool: ErrOr[Boolean] = Valid(true)
  val invalidInt: ErrOr[Int] = Invalid("Invalid int.")
  val invalidBool: ErrOr[Boolean] = Invalid("Invalid boolean.")

  Apply[ErrOr].productR(validInt)(validBool)                  // res: ErrOr[Boolean] = Valid(true)
  Apply[ErrOr].productR(invalidInt)(validBool)                // res: ErrOr[Boolean] = Invalid("Invalid int.")

  Apply[ErrOr].productR(validInt)(invalidBool)                // res: ErrOr[Boolean] = Invalid("Invalid boolean.")
  Apply[ErrOr].productR(invalidInt)(invalidBool)              // res: ErrOr[Boolean] = Invalid("Invalid int.Invalid boolean.")


  Apply[ErrOr].productL(validInt)(validBool)                  // res: ErrOr[Int] = Valid(3)
  Apply[ErrOr].productL(invalidInt)(validBool)                // res: ErrOr[Int] = Invalid("Invalid int.")

  Apply[ErrOr].productL(validInt)(invalidBool)                // res: ErrOr[Int] = Invalid("Invalid boolean.")
  Apply[ErrOr].productL(invalidInt)(invalidBool)              // res: ErrOr[Int] = Invalid("Invalid int.Invalid boolean.")
  
  
  /* 
     Aliases (given by Cats):
     <*> is an alias for ap()
     *> is an alias for productR
     <* is an alias for productL
  */

}
