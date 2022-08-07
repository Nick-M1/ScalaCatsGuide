package data_types.ValidatedDemo

import cats.data._
import cats.data.Validated._
import cats.implicits._

// Attempt of a validation form, with Validation

object ValidationForm_v2 extends App {

  // Registration Form Data:
  final case class RegistrationData(username: String, password: String, firstName: String, lastName: String, age: Int)


  // Error Messages:
  sealed trait DomainValidation {
    def errorMessage: String
  }

  case object UsernameHasSpecialCharacters extends DomainValidation {
    def errorMessage: String = "Username cannot contain special characters."
  }

  case object PasswordDoesNotMeetCriteria extends DomainValidation {
    def errorMessage: String = "Password must be at least 10 characters long, including an uppercase and a " +
      "lowercase letter, one number and one special character."
  }

  case object FirstNameHasSpecialCharacters extends DomainValidation {
    def errorMessage: String = "First name cannot contain spaces, numbers or special characters."
  }

  case object LastNameHasSpecialCharacters extends DomainValidation {
    def errorMessage: String = "Last name cannot contain spaces, numbers or special characters."
  }

  case object AgeIsInvalid extends DomainValidation {
    def errorMessage: String = "You must be aged 18 and not older than 75 to use our services."
  }





  // Validates forms (Logic):
  sealed trait FormValidatorNec {

    type ValidationResult[A] = ValidatedNec[DomainValidation, A]    // type alias, as all our Validated types will have DomainValidation as a type

    private def validateUserName(userName: String): ValidationResult[String] =
      if (userName.matches("^[a-zA-Z0-9]+$")) then userName.validNec        // .validNec & .invalidNec are combinators that lift the value
      else UsernameHasSpecialCharacters.invalidNec                                //   to its respective container (Valid or Invalid)

    private def validatePassword(password: String): ValidationResult[String] =
      if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) then password.validNec
      else PasswordDoesNotMeetCriteria.invalidNec

    private def validateFirstName(firstName: String): ValidationResult[String] =
      if (firstName.matches("^[a-zA-Z]+$")) then firstName.validNec
      else FirstNameHasSpecialCharacters.invalidNec

    private def validateLastName(lastName: String): ValidationResult[String] =
      if (lastName.matches("^[a-zA-Z]+$")) then lastName.validNec
      else LastNameHasSpecialCharacters.invalidNec

    private def validateAge(age: Int): ValidationResult[Int] =
      if (age >= 18 && age <= 75) then age.validNec
      else AgeIsInvalid.invalidNec


    // Main function that runs the validation-checking code
    def validateForm(username: String, password: String, firstName: String, lastName: String, age: Int): ValidationResult[RegistrationData] = {
      (validateUserName(username),                            // .mapN() used to accumulate all the validations together
        validatePassword(password),                           //    if there is an error -> will return all the errors
        validateFirstName(firstName),                         //    if there are no errors -> will create an instance of RegistrationData
        validateLastName(lastName),                           //      using its constructor
        validateAge(age)).mapN(RegistrationData.apply)
    }

  }

  object FormValidatorNec extends FormValidatorNec

  /* The logic of the validation process is as follows:
  *  - Check every individual field based on the established rules for each one of them.
  *  - If the validation is successful, then return the field wrapped in a Valid instance;
  *  - If not, then return a DomainValidation with the respective message, wrapped in an Invalid instance.
  *
  *  Our service has the validateForm method for checking all the fields and,
  *   if the process succeeds it will create an instance of RegistrationData
  *   however, if a check fails, it will carry on with the other checks (not fail-fast) and will therefore show all errors to user.
  *
  *  Validation is an Applicative, so it doesn't have a flatMap method (which is from Monad), so can't use a for-comprehension
  *
  * */


  // Testing:
  FormValidatorNec.validateForm(
    username = "Joe",
    password = "Passw0r$1234",
    firstName = "John",
    lastName = "Doe",
    age = 21
  )
  // res: FormValidatorNec.ValidationResult[RegistrationData] = Valid(
  //   RegistrationData("Joe", "Passw0r$1234", "John", "Doe", 21)
  //  )


  FormValidatorNec.validateForm(
    username = "Joe%%%",
    password = "password",
    firstName = "John",
    lastName = "Doe",
    age = 21
  )
  // res: FormValidatorNec.ValidationResult[RegistrationData] = Invalid(
  //   Chain(UsernameHasSpecialCharacters, PasswordDoesNotMeetCriteria)
  //  )


}
