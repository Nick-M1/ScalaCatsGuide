package datatypes.NestedDemo

import cats.data.Nested
import cats.implicits._
import cats.data.Validated
import cats.data.Validated._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.Applicative

/* Nested is similar to monad transformers like OptionT and EitherT, as it represents the nesting of effects inside each other.
*   But Nested is more general - it does not place any restriction on the type of the two nested effects.
*
*   Instead, it provides a set of inference rules based on the properties of F[_] and G[_]:
*   - If F[_] and G[_] are both Functors, then Nested[F, G, *] is also a Functor
*   - If F[_] and G[_] are both Applicatives, then Nested[F, G, *] is also an Applicative
*   - If F[_] is an ApplicativeError and G[_] is an Applicative, then Nested[F, G, *] is an ApplicativeError
*   - If F[_] and G[_] are both Traverses, then Nested[F, G, *] is also a Traverse
* */


object NestedDemo {

  // DATA-STRUCTURE:
  final case class myNested[F[_], G[_], A](value: F[G[A]])



  // USE-CASE:

  /* When you have data insided nested effects.
  *  These can be difficult to work with (e.g. mapping) */
  val x: Option[Validated[String, Int]] = Some(Valid(123))
  x.map(_.map(_.toString))        // Difficult to work on the data inside

  /* Use nested to access / work on data inside easily */
  val nested: Nested[Option, Validated[String, *], Int] = Nested(Some(Valid(123)))
  nested.map(_.toString).value



  // EXAMPLE:

  // API for creating users
  case class UserInfo(name: String, age: Int)
  case class User(id: String, name: String, age: Int)

  def createUser(userInfo: UserInfo): Future[Either[List[String], User]] =
    Future.successful(Right(User("user 123", userInfo.name, userInfo.age)))


  // Given a list of UserInfos, creates a list of Users
  def createUsers(userInfos: List[UserInfo]): Future[Either[List[String], List[User]]] =
    userInfos.traverse(userInfo => Nested(createUser(userInfo))).value

  val userInfos: List[UserInfo] = List(
    UserInfo("Alice", 42),
    UserInfo("Bob", 99)
  )

  Await.result(createUsers(userInfos), 1.second)
  // res: Either[List[String], List[User]] = Right(
  //   List(User("user 123", "Alice", 42), User("user 123", "Bob", 99))
  // )
  

  // However, if we didnt use Nested:
  def createUsersNotNested(userInfos: List[UserInfo]): Future[List[Either[List[String], User]]] =
    userInfos.traverse(createUser)

  Await.result(createUsersNotNested(userInfos), 1.second)
  // res3: List[Either[List[String], User]] = List(
  //   Right(User("user 123", "Alice", 42)),
  //   Right(User("user 123", "Bob", 99))
  // )


}
