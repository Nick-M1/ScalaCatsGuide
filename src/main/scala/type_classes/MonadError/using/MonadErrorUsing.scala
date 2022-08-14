package type_classes.MonadError.using

import cats.MonadError
import cats.implicits._

object MonadErrorUsing extends App {


  def getCityClosestToCoordinate[F[_]](x: (Int, Int))(using me: MonadError[F, String]): F[String] = x match {
    case (a, b) if a < 0 || b < 0 => me.raiseError("incorrect coords")
    case _ => me.pure("Minneapolis, MN")
  }

  def getTemperatureByCity[F[_]](city: String)(using me: MonadError[F, String]): F[Int] = {
    me.pure(78) // If getCityCoord didn't return raiseError, then returns 78, else passes the error on
  }

  // MonadError allows use of flatMap (and therefore For-Comprehensions) from inheriting Monad
  def getTemperatureByCoordinates[F[_]](x: (Int, Int))(using F: MonadError[F, String]): F[Int] = {
    for {
      c <- getCityClosestToCoordinate[F](x)
      t <- getTemperatureByCity[F](c)
    } yield t
  }

  type MyEither[A] = Either[String, A]

  getTemperatureByCoordinates[MyEither]((44, 93)) // Right(78)
  getTemperatureByCoordinates[MyEither]((-44, 93)) // Left(incorrect coords)


}
