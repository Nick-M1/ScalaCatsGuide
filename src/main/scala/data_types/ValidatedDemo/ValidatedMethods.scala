package data_types.ValidatedDemo

import cats.{Applicative, Bifunctor, Eq, Eval, Order, PartialOrder, Semigroup, Show}
import cats.data.{Ior, Validated, ValidatedNec, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.*

object ValidatedMethods extends App {

  /** fold: <p>
   *  Same as the scala standard libary
   * */
  val v1 = "error".invalid[Option[String]]
  val v2 = Some("abc").valid[String]

  v1.fold(identity, _.getOrElse(""))      // res: String = "error"
  v2.fold(identity, _.getOrElse(""))      // res: String = "abc"


  /** isValid: <br>
   *  returns true if the type is of Valid() and false if not
   *
   *  isInvalid: <br>
   *  returns if the type is of Invalid() and false if not
   */
  v1.isValid          // res: Boolean = false
  v1.isInvalid        // res: Boolean = true

  v2.isValid          // res: Boolean = true
  v2.isInvalid        // res: Boolean = false


  /** foreach: <p>
   * Run the side-effecting function on the value if it is Valid
   */

  v1.foreach(x => println(s"$x is Valid"))         // Nothing printed, as v1 is invalid
  v2.foreach(x => println(s"$x is Valid"))         // >> "is Valid" , as v2 is valid

  /** getOrElse: <p>
   * Return the Valid value, or the default if Invalid
   */
  val v3 = "error".invalid[Int]
  val v4 = 123.valid[String]

  v3.getOrElse(456)               // res: Int = 456 -> v3 is the Invalid type, so returns the default 'orElse' value
  v4.getOrElse(456)               // res: Int = 123 -> v4 is the Valid type, so returns the actual value of v4


  /** valueOr: <p>
   * Return the Valid value, or the result of f if Invalid
   * */
  val v5 = Some("exception").invalid[String]
  val v6 = "OK".valid[Option[String]]

  v5.valueOr(_.getOrElse(""))           // res: String = "exception"
  v6.valueOr(_.getOrElse(""))           // res: String = "OK"


  /** exists: <p>
   * returns true if this is Valid and matching the given predicate  */
  val v7 = Some("error").invalid[Int]
  val v8 = 123.valid[List[String]]

  v7.exists(_ > 120)                      // res: Boolean = false -> v7 is Invalid (not Valid)
  v8.exists(_ < 120)                      // res: Boolean = false -> v8 is Valid, but doesn't match the predicate

  v8.exists(_ > 120)                      // res: Boolean = true -> v8 is Valid & matches the predicate

  /** forall:
   * Is this Invalid or matching the predicate
   */
  v7.forall(_ > 120) // res: Boolean = true -> v7 is Invalid
  v8.forall(_ < 120) // res: Boolean = false -> v8 is Valid, but doesn't match the predicate

  v8.forall(_ > 120) // res: Boolean = true -> v8 is Valid & matches the predicate


  /** orElse: <p>
   * Return this if it is Valid, or else fall back to the given default.
   * The functionality is similar to that of ''findValid'' except for failure accumulation,
   *   where here only the error on the right is preserved and the error on the left is ignored.
   */
  val defaultValidated = 456.valid[Option[String]]
  v7.orElse(defaultValidated)               // res: Validated[Option[String], Int] = Valid(456)   -> Uses default as v7 is Invalid
  v8.orElse(defaultValidated)               // res: Validated[Option[String], Int] = Valid(123)   -> Doesn't use default as v8 is Valid


  /** findValid: <p>
   * If this is valid return this, otherwise if that is valid return `that`, otherwise combine the failures.
   * This is similar to ''orElse'' except that here failures are accumulated.
   */
  val v9 = List("error1").invalid[Int]
  val v10 = 123.valid[List[String]]

  val default1 = List("error2").invalid[Int]
  val default2 = 456.valid[List[String]]

  v9.findValid(default1)            // res: Validated[List[String], Int] = Invalid(List(error1, error2)) -> accumilates errors as both v9 & default1 are Invalid
  v9.findValid(default2)            // res: Validated[List[String], Int] = Valid(456)
  v10.findValid(default1)           // res: Validated[List[String], Int] = Valid(123)



  /** toEither:
   * Converts the value to an Either[E, A]
   */
  val v11 = "error".invalid[Int]
  val v12 = 123.valid[String]

  v11.toEither      // res: Either[String, Int] = Left(error)
  v12.toEither      // res: Either[String, Int] = Right(123)

  /** toOption: <p>
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  val v13 = List("error").invalid[Int]
  val v14 = 123.valid[List[String]]

  v13.toOption          // res: Option[Int] = None
  v14.toOption          // res: Option[Int] = Some(123)



  /** toList: <br>
   * Convert this value to a single element List if it is Valid, otherwise return an empty List <p>
   *
   * toValidatedNel: <br>
   * Lift the Invalid value into a NonEmptyList. <p>
   *
   * toValidatedNec: <br>
   * Lift the Invalid value into a NonEmptyChain.
   */
  val v15 = "error".invalid[Int]
  val v16 = 123.valid[String]

  v15.toList              // res: List[Int] = List()
  v16.toList              // res: List[Int] = List(123)

  v15.toValidatedNel      // res: ValidatedNel[String, Int] = Invalid(NonEmptyList(error))
  v16.toValidatedNel      // res: ValidatedNel[String, Int] = Valid(123)

  v15.toValidatedNec      // res: ValidatedNec[String, Int] = Invalid(Chain(error))
  v16.toValidatedNec      // res: ValidatedNec[String, Int] = Valid(123)



  /** compare: <br>
   *  Compares 2 Validated types, returning -1, 0 or 1
   *
   *  "===": <br>
   *  Returns true if 2 Validated are equal to eachother
   */
  val v17 = "error".invalid[Int]
  val v18 = "error2".invalid[Int]
  val v19 = 123.valid[String]
  val v20 = 456.valid[String]
  val v21 = v20

  v17 compare v18         // res: Int = -1
  v17 compare v19         // res: Int = -1
  v19 compare v17         // res: Int = 1
  v19 compare v20         // res: Int = -1
  v20 compare v21         // res: Int = 0

  v17 === v18             // res: Boolean = false
  v20 === v21             // res: Boolean = true



  /** ap: <p>
   * if both the function and this value are Valid, apply the function <br>
   * From Apply type-class
   */
  val v22 = "error".invalid[Int]
  val v23 = 123.valid[String]
  val func: Validated[String, Int => Option[Int]] = (Option.apply[Int] _).valid[String]

  v22.ap(func)                // res: Validated[String, Option[Int]] = Invalid(error)
  v23.ap(func)                // res: Validated[String, Option[Int]] = Valid(Some(123))

  /** product: <p>
   * Combines 2 Validated together. <br>
   * If atleast 1 Validated is an Invalid, then only returns the Invalid <br>
   * From Product type-class
   */
  val v24 = "error".invalidNec[Int]
  val v25 = "error2".invalidNec[Int]
  val v26 = 123.validNec[String]
  val v27 = 456.validNec[String]

  v24.product(v25)          // res: ValidatedNec[String, (Int, Int)] = Invalid(Chain(error, error2))
  v24.product(v26)          // res: ValidatedNec[String, (Int, Int)] = Invalid(Chain(error))
  v26.product(v27)          // res: ValidatedNec[String, (Int, Int)] = Valid((123, 456))

  /** map: <p>
   * Apply a function to a Valid value, returning a new Valid value
   */
  val v28a = "error".invalid[Int]
  val v28b = 123.valid[String]
  v28a.map(_ * 2)                // res: Validated[String, Int] = Invalid(error)
  v28b.map(_ * 2)                // res: Validated[String, Int] = Valid(246)


  /** leftMap: <p>
   * Apply a function to an Invalid value, returning a new Invalid value. <br>
   * Or, if the original valid was Valid, return it. <p>
   * The opposite to ''map'', with regards to the way it applies to Valid & Invalid
   */
  val v29 = "error".invalid[Int]
  val v30 = 123.valid[String]

  v29.leftMap(Option.apply)       // res: Validated[Option[String], Int] = Invalid(Some(error))
  v30.leftMap(Option.apply)       // res: Validated[Option[String], Int] = Valid(123)


  /** traverse: <p>
   * When Valid, apply the function, marking the result as valid inside the Applicative's context, <br>
   * when Invalid, lift the Error into the Applicative's context.
   *
   * foldLeft: <br>
   * apply the given function to the value with the given B when valid, otherwise return the given B <p>
   *
   * foldRight: <br>
   * Lazily-apply the given function to the value with the given B when valid, otherwise return the given B.
   */
  val v31 = "error".invalid[Int]
  val v32 = 123.valid[String]

  v31.traverse(Option.apply[Int])         // res: Option[Validated[String, Int]] = Some(Invalid(error))
  v32.traverse(Option.apply[Int])         // res: Option[Validated[String, Int]] = Some(Valid(123))

  v31.foldLeft(456)(_ + _)                // res: Int = 456
  v32.foldLeft(456)(_ + _)                // res: Int = 579

  v31.foldRight(Eval.now(456))((i, e) => e.map(_ + i))          // res: Eval[Int] = Now(456)
  v32.foldRight(Eval.now(456))((i, e) => e.map(_ + i)).value    // res: Int = 579



  /** andThen: <p>
   * Apply a function (that returns a `Validated`) in the valid case. <br>
   * Otherwise return the original `Validated`. <p>
   *
   * This allows "chained" validation: the output of one validation can be fed into another validation function.
   */
  val v33 = "error".invalid[Int]
  val v34 = 123.valid[String]
  val func2: Int => Validated[String, List[Int]] = List(_).valid[String]

  v33.andThen(func2)          // res: Validated[String, List[Int]] = Invalid(error)
  v34.andThen(func2)          // res: Validated[String, List[Int]] = Valid(List(123))

  /** combine: <p>
   * Combine this `Validated` with another `Validated`, using the `Semigroup`
   * instances of the underlying `E` and `A` instances. The resultant `Validated`
   * will be `Valid`, if, and only if, both this `Validated` instance and the
   * supplied `Validated` instance are also `Valid`.
   */
  val v35 = "error".invalidNel[List[Int]]
  val v36 = "error2".invalidNel[List[Int]]
  val v37 = List(123).validNel[String]
  val v38 = List(456).validNel[String]

  v35 combine v36         // res: Validated[NonEmptyList[String], List[Int]] = Invalid(NonEmptyList(error, error2))
  v36 combine v37         // res: Validated[NonEmptyList[String], List[Int]] = Invalid(NonEmptyList(error2))
  v37 combine v38         // res: Validated[NonEmptyList[String], List[Int]] = Valid(List(123, 456))

  /** swap: <p>
   *  If applied on a Valid, changes it to Invalid <br>
   *  If applied on an Invalid, changes it to Valid
   */
  val v39 = "error".invalid[Int]
  val v40 = 123.valid[String]

  v39.swap                // res: Validated[Int, String] = Valid(error)
  v40.swap                // res: Validated[Int, String] = Invalid(123)


  /** ensure: <p>
   * Ensure that a successful result passes the given predicate, falling back to an Invalid of `onFailure` if the predicate returns false.
   */
  val v41 = Validated.valid("")

  v41.ensure(new IllegalArgumentException("Must not be empty"))(_.nonEmpty)
    // res: Validated[IllegalArgumentException, String] = Invalid(java.lang.IllegalArgumentException: Must not be empty)

  /** ensureOr: <p>
   * Ensure that a successful result passes the given predicate, falling back to the an Invalid of the result of `onFailure` if the predicate returns false.
   */
  val v42 = Validated.valid("ab")

  v42.ensureOr(s => new IllegalArgumentException("Must be longer than 3, provided '" + s + "'"))(_.length > 3)
    // res: Validated[IllegalArgumentException, String] = Invalid(java.lang.IllegalArgumentException: Must be longer than 3, provided 'ab')

}
