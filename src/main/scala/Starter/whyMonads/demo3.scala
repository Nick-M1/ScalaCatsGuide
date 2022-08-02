package Starter.whyMonads

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/** Example #2: Asynchronous Fetches <p>
    Calling an async services for an online store, (fetching from some external resource)    */


object demo3 {

  case class User(id: String)
  case class Product(sku: String, price: Double) // currency should be in BigDecimal

  def getUser(url: String): Future[User] = Future( User("1234") )                       // Business logic in future
  def getLastOrder(userId: String): Future[Product] = Future( Product("1234", 25.3) )   // Business logic in future

  val userFuture: Future[User] = getUser("my.store.com/users/daniel")


  // #1 Using onComplete - BAD
    userFuture.onComplete {
      case Success(User(id)) =>
        val lastOrder = getLastOrder(id)
        lastOrder.onComplete {
          case Success(Product(_, p)) =>
            val vatIncludedPrice = p * 1.19
        }
    }

  // #2 - Using flatMap (flatMap will wait for the Future to finish before going onto next stage
  val vatIncludedPrice: Future[Product] = userFuture.flatMap(user => getLastOrder(user.id)) // relevant monad bit

  // #3 - Using For-comp
  val vatIncludedPriceFor: Future[Double] = for {
    user <- userFuture
    order <- getLastOrder(user.id)
  } yield order.price * 1.19



  /* For-comp example. checkerboard1 == checkerboard2, but checkerboard2 is easier to read */
  val numbers: List[Int] = List(1,2,3)
  val chars: List[Char] = List('a', 'b', 'c')

  val checkerboard1: List[(Int, Char)] = numbers.flatMap(number => chars.map(char => (number, char)))

  val checkerboard2: List[(Int, Char)] = for {
    n <- numbers
    c <- chars
  } yield (n, c)


}