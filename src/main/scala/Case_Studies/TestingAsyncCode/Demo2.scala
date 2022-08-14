package Case_Studies.TestingAsyncCode


import cats.instances.future._ // for Applicative
import cats.instances.list._ // for Traverse
import cats.syntax.traverse._ // for traverse
import cats.Id

import cats.Applicative
import cats.syntax.functor._ // for map

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/** Better implementation: <p>
 * What result type should UptimeClient give? <br>
 * We want to retain the Int part from each type but “throw away” the Future part in the test code. <br>
 * Sol: Cat's Id allows us to “wrap” types in a type constructor without changing their meaning <br>
 * */
object Demo2 {

  type Id[A] = A

  trait UptimeClient[F[_]] {
    def getUptime(hostname: String): F[Int]
  }

  // Implement 2 versions of UptimeClient: an asynchronous one for use in production and a synchronous one for use in our unit tests:
  trait RealUptimeClient extends UptimeClient[Future] {
    def getUptime(hostname: String): Future[Int]
  }

//  trait TestUptimeClient extends UptimeClient[Id] {
//    def getUptime(hostname: String): Int          // Id[Int] == Int (Id is an alias wrapper)
//  }

  // fleshed out version of trait TestUptimeClient
  class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient[Id] { // no longer need the call to Future.successful
    def getUptime(hostname: String): Int =
      hosts.getOrElse(hostname, 0)
  }

  // Issue: Traversing a List[ F[Int]], but F has no built-in Applicative (as it is generic type).
  // So add a context bound for F  ([F[_]: Applicative]) so compiler knows that generic F is also an Applicative with traverse method
  // Instead, could use an implicit parameter in the constructor (commented out below)
  class UptimeService[F[_] : Applicative](client: UptimeClient[F]) {
    def getTotalUptime(hostnames: List[String]): F[Int] =
      hostnames.traverse(client.getUptime).map(_.sum)
  }

  //  class UptimeService[F[_]](client: UptimeClient[F])(implicit a: Applicative[F]) {
  //    def getTotalUptime(hostnames: List[String]): F[Int] =
  //      hostnames.traverse(client.getUptime).map(_.sum)
  //  }


  def testTotalUptime(): Unit = {
    val hosts = Map("host1" -> 10, "host2" -> 6)
    val client = new TestUptimeClient(hosts)
    val service = new UptimeService(client)
    val actual = service.getTotalUptime(hosts.keys.toList)
    val expected = hosts.values.sum
    assert(actual == expected)
  }

  def main(args: Array[String]): Unit = {
    testTotalUptime()
  }

}
