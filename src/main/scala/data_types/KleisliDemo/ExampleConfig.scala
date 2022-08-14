package data_types.KleisliDemo

import cats.data.Kleisli

/** Let's look at some example modules, where each module has its own configuration that is validated by a function.
    If configuration is good -> return a Some of the module, otherwise a None.

    This example uses Option, but could use Either to provide error messages / error handling.                 */


object ExampleConfig {

  // Database component
  case class DbConfig(url: String, user: String, pass: String)
  trait Db
  object Db {
    val fromDbConfig: Kleisli[Option, DbConfig, Db] = ???
  }

  // Service component
  case class ServiceConfig(addr: String, port: Int)
  trait Service
  object Service {
    val fromServiceConfig: Kleisli[Option, ServiceConfig, Service] = ???
  }


  /* We have two independent modules: a Db (allowing access to a database) and a Service (supporting an API to provide data over the web).
     Both depend on their own configuration parameters.
     Neither know or care about the other, as it should be.
     However our app needs both of these modules to work -> Have a global application configuration. */
  case class AppConfig(dbConfig: DbConfig, serviceConfig: ServiceConfig)
  class App(db: Db, service: Service)


  /* Right now, can't use both Kleisli validation functions together easily - one takes a DbConfig, the other a ServiceConfig.
     That means the FlatMap (and by extension, the Monad) instances differ (recall the input type is fixed in the type class instances).
     However, there is a nice function on Kleisli called local.

     final case class Kleisli[F[_], A, B](run: A => F[B]) {
       def local[AA](f: AA => A): Kleisli[F, AA, B] = Kleisli(f.andThen(run))
     }

     local allows us to "expand" our input type to a more "general" one.
     In our case, we can take a Kleisli that expects a DbConfig or ServiceConfig and turn it into one that expects an AppConfig,
     as long as we tell it how to go from an AppConfig to the other configs.
  */

  def appFromAppConfig: Kleisli[Option, AppConfig, App] = for {
    db <- Db.fromDbConfig.local[AppConfig](_.dbConfig)
    sv <- Service.fromServiceConfig.local[AppConfig](_.serviceConfig)
  } yield new App(db, sv)

}
