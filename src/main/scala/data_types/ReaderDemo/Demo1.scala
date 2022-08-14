package data_types.ReaderDemo

import cats.data.Reader
import cats.implicits._

/** Readers provide a tool for doing dependency injection. <p>
   We write steps of our program as instances of Reader, chain them together with map and flatMap, and build a function that accepts the dependency as input. */

object Demo1 {

  final case class Cat(name: String, favoriteFood: String)

  val greetKitty: Reader[Cat, String] = Reader(cat => s"Hello ${cat.name}")   // = cat.map( cat => s"Hello ${cat.name}" )
  val feedKitty: Reader[Cat, String] = Reader(cat => s"Have a nice bowl of ${cat.favoriteFood}")

  val greetAndFeed: Reader[Cat, String] = for {     // Combines greetKitty & feedKitty
    greet <- greetKitty
    feed <- feedKitty
  } yield s"$greet. $feed."


  greetAndFeed(Cat("Garfield", "lasagne"))      // res: cats.package.Id[String] = "Hello Garfield. Have a nice bowl of lasagne."
  greetAndFeed(Cat("John", "junk food"))        // res: cats.package.Id[String] = "Hello John. Have a nice bowl of junk food."


}
