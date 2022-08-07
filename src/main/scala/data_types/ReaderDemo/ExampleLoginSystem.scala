package data_types.ReaderDemo

import cats.data.Reader
import cats.syntax.applicative._ // for pure

/** Reading from a DB & checking if items exist in DB (e.g. finding by userID) */

object ExampleLoginSystem {

  final case class Db(usernames: Map[Int, String], passwords: Map[String, String])

  type DbReader[A] = Reader[Db, A]    // Alias for Reader


  def findUsername(userId: Int): DbReader[Option[String]] =                       // Returns the username
    Reader(db => db.usernames.get(userId))

  def checkPassword(username: String, password: String): DbReader[Boolean] =
    Reader(db => db.passwords.get(username).contains(password))                   // Returns a boolean -> depends on if the password exists for this username

  def checkLogin(userId: Int, password: String): DbReader[Boolean] = for {        // Combines findUsername & checkPassword
    username <- findUsername(userId)
    passwordValid <- username.map( username => checkPassword(username, password) ).getOrElse( false.pure[DbReader] )
  } yield passwordValid


  val users: Map[Int, String] = Map(
    1 -> "dade",
    2 -> "kate",
    3 -> "margo"
  )

  val passwords: Map[String, String] = Map(
    "dade" -> "zerocool",
    "kate" -> "acidburn",
    "margo" -> "secret"
  )

  val db: Db = Db(users, passwords)

  checkLogin(1, "zerocool").run(db)     // res7: cats.package.Id[Boolean] = true
  checkLogin(4, "davinci").run(db)      // res8: cats.package.Id[Boolean] = false


}
