package data_types.ValidatedDemo

// Attempt of a validation form, with Either

object ValidationForm_v1 extends App {

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




  import cats.implicits._

  // Validates forms (Logic):
  sealed trait FormValidator {
    def validateUserName(userName: String): Either[DomainValidation, String] =
      Either.cond(
        userName.matches("^[a-zA-Z0-9]+$"),     // Condition (returns boolean)
        userName,                                     // If condition is true, return 2nd argument
        UsernameHasSpecialCharacters                  // Else if condition is false, return 3rd argument
      )

    def validatePassword(password: String): Either[DomainValidation, String] =
      Either.cond(
        password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"),
        password,
        PasswordDoesNotMeetCriteria
      )

    def validateFirstName(firstName: String): Either[DomainValidation, String] =
      Either.cond(
        firstName.matches("^[a-zA-Z]+$"),
        firstName,
        FirstNameHasSpecialCharacters
      )

    def validateLastName(lastName: String): Either[DomainValidation, String] =
      Either.cond(
        lastName.matches("^[a-zA-Z]+$"),
        lastName,
        LastNameHasSpecialCharacters
      )

    def validateAge(age: Int): Either[DomainValidation, Int] =
      Either.cond(
        age >= 18 && age <= 75,
        age,
        AgeIsInvalid
      )

    // Main function that runs the validation-checking code
    def validateForm(username: String, password: String, firstName: String, lastName: String, age: Int): Either[DomainValidation, RegistrationData] = {
      for {
        validatedUserName <- validateUserName(username)
        validatedPassword <- validatePassword(password)
        validatedFirstName <- validateFirstName(firstName)
        validatedLastName <- validateLastName(lastName)
        validatedAge <- validateAge(age)
      } yield RegistrationData(validatedUserName, validatedPassword, validatedFirstName, validatedLastName, validatedAge)
    }

  }

  object FormValidator extends FormValidator

  /* The logic of the validation process is as follows:
  *  - Check every individual field based on the established rules for each one of them.
  *  - If the validation is successful, then return the field wrapped in a Right instance;
  *  - If not, then return a DomainValidation with the respective message, wrapped in a Left instance.
  *
  *  Our service has the validateForm method for checking all the fields and,
  *   if the process succeeds it will create an instance of RegistrationData
  *   however, if a check fails, it will fail-fast (and not do the other checks), therefore only showing the first error.
  *
  *  A for-comprehension is fail-fast. If some of the evaluations in the for block fails for some reason,
  *   the yield statement will not complete. In our case, if that happens we won't be getting the accumulated list of errors.
  *
  * */


  // Testing:
  FormValidator.validateForm(
    username = "fakeUs3rname",
    password = "password",
    firstName = "John",
    lastName = "Doe",
    age = 15
  )
  // res: Either[DomainValidation, RegistrationData] = Left( PasswordDoesNotMeetCriteria )
  // It doesn't show the error for incorrect age.

}
