package data_types.EitherDemo

import cats.syntax.all._
import cats.implicits._

object EitherDemo2 extends App {

  // Database layer
  sealed abstract class DatabaseError

  trait DatabaseValue

  object Database {
    def databaseThings(): Either[DatabaseError, DatabaseValue] = ???
  }

  // Service layer
  sealed abstract class ServiceError

  trait ServiceValue

  object Service {
    def serviceThings(v: DatabaseValue): Either[ServiceError, ServiceValue] = ???
  }

  // Error-handling ADT:
  sealed abstract class AppError

  object AppError {
    final case class Database(error: DatabaseError) extends AppError

    final case class Service(error: ServiceError) extends AppError
  }

  // May contain errors from both Database & Service (which are different types of Error)
  def doApp(): Either[AppError, ServiceValue] =
    Database.databaseThings().leftMap[AppError](AppError.Database.apply).
      flatMap(dv => Service.serviceThings(dv).leftMap[AppError](AppError.Service.apply))

  // Error-handling
  def doAppHandler() = doApp() match {
    case Left(AppError.Database(_)) => "something in the database went wrong"
    case Left(AppError.Service(_)) => "something in the service went wrong"
    case Right(_) => "everything is alright!"
  }


}
