package Case_Studies.DataValidation

import cats.Semigroup
import cats.instances.list._ // for Semigroup
import cats.syntax.either._ // for asLeft and asRight
import cats.syntax.semigroup._ // for |+|

/**  Program to check that user's input meets certain criteria.
 * 
 *  E.g. usernames must not be blank, email addresses must be valid, and so on.
 *
 *  Our checks:
 *  - A user must be over 18 years old or must have parental consent.
 *  - A String ID must be parsable as a Int and the Int must correspond to a valid record ID.
 *  - A bid in an auction must apply to one or more items and have a positive value.
 *  - A username must contain at least four characters and all characters must be alphanumeric.
 *  - An email address must contain a single @ sign. Split the string at the @. The string to the left must not be empty.
 *        The string to the right must be at least three characters long and contain a dot.
 *
 *  Extra goals:
 *  - We should be able to associate meaningful messages with each validation failure, so the user knows why their data is not valid.
 *  - We should be able to combine small checks into larger ones. Taking the username example above, we should be able to express this
 *        by combining a check of length and a check for alphanumeric values.
 *  - We should be able to transform data while we are checking it. There is an example above requiring we parse data, changing its type from String to Int.
 *  - Finally, we should be able to accumulate all the failures in one go, so the user can correct all the issues before resubmitting.
 * */

object Demo1 extends App {

  // Error Messages:
  // Use a Monoid & accumulate the error messages in a List

  /** Implementation v1: <p>
     Represent checks as functions. <br>
     The Check data type becomes a simple wrapper for a function that provides our library of combinator methods.     */
  
  // Check error type (E) could be a String, Exception or custom type...
  type Check[E, A] = A => Either[E, A] 

  val semigroup = Semigroup[List[String]] // for combining error messages

  final case class CheckF[E, A](func: A => Either[E, A]) {
    
    // Function to apply a check to a value & return Either
    def apply(a: A): Either[E, A] =
      func(a)
    
    // Function to chain error-checking functions together 
    def and(that: CheckF[E, A])(implicit s: Semigroup[E]): CheckF[E, A] = 
      CheckF { a =>
        (this (a), that(a)) match {
          case (Left(e1), Left(e2)) => (e1 |+| e2).asLeft     // Both CheckF's contains error msgs -> combine
          case (Left(e), Right(_)) => e.asLeft                // Only 1st CheckF contains error msg -> only return 1st error msg (ignore the empty 2nd CheckF)
          case (Right(_), Left(e)) => e.asLeft                //   ^^^ same but for 2nd CheckF
          case (Right(_), Right(_)) => a.asRight              // Neither CheckF contains error msgs -> return no error msgs
        }
      }
  }


  // Testing:
  val a: CheckF[List[String], Int] = CheckF(v => if (v > 2) then v.asRight else List("Must be > 2").asLeft)
  val b: CheckF[List[String], Int] = CheckF(v => if (v < -2) then v.asRight else List("Must be < -2").asLeft)
  val check: CheckF[List[String], Int] = a and b // a.and(b) -> this should always give error as a num can't be x < -2 & x > 2

  check(5) // res: Either[List[String], Int] = Left(List("Must be < -2"))
  check(0) // res: Either[List[String], Int] = Left(List("Must be > 2", "Must be < -2"))    -> returns both errors


  // Issue with this implementation: create checks that fail with a type that can't accumulate. Below using type Nothing as accumulator
  val c: CheckF[Nothing, Int] = CheckF(v => v.asRight)
  val d: CheckF[Nothing, Int] = CheckF(v => v.asRight)
//  val check2 = c and d        // Error: could not find implicit value for parameter Semigroup[Nothing]


}
