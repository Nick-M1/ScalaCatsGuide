package data_types.FunctionKDemo

import cats.arrow.FunctionK


/*  A FunctionK transforms values from one first-order-kinded type (a type that takes a single type parameter,
     such as List or Option) into another first-order-kinded type.

    This transformation is universal, meaning that a FunctionK[List, Option] will translate all List[A] values
     into an Option[A] value for all possible types of A.                                                          */


object Demo1 {

  // Function to convert an item in a List -> item in an Option: (generic for any type in container)
  def firstFuncGen[A](l: List[A]): Option[A] = l.headOption


  // Abstract the container types (List & Option) into a typeclass
  trait MyFunctionK[F[_], G[_]] {
    def apply[A](fa: F[A]): G[A]
  }


  // Same as myFunctionK, but using Cat's builtin FunctionK
  val first: FunctionK[List, Option] = new FunctionK[List, Option] {
    def apply[A](l: List[A]): Option[A] = l.headOption
  }

  // Make it implicit
  given FunctionK[List, Option] with {
    def apply[A](l: List[A]): Option[A] = l.headOption
  }



  // Types with more than one type parameter:
  type ErrorOr[A] = Either[String, A]         // type alias - FunctionK only works with types with 1 generic, so fix 1 of the types of Either to String

  val errorOrFirst: FunctionK[List, ErrorOr] = new FunctionK[List, ErrorOr] {
    def apply[A](l: List[A]): ErrorOr[A] = l.headOption.toRight("Error: list was empty")
  }

}
