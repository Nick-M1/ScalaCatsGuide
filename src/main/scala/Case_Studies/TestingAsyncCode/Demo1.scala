package Case_Studies.TestingAsyncCode

import cats.Id
import cats.instances.future.*
import cats.instances.list.*
import cats.syntax.traverse.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/** Simplify unit tests for asynchronous code by making them synchronous */


object Demo1 {
  
  // Modelled UptimeClient as a trait so can stub it out in unit tests.
  trait UptimeClient { 
    def getUptime(hostname: String): Future[Int]
  }

  class UptimeService(client: UptimeClient) {
    def getTotalUptime(hostnames: List[String]): Future[Int] =
      hostnames.traverse(client.getUptime).map(_.sum)
  }


  // Test client that allows us to provide dummy data rather than calling out to actual servers:
  class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient {
    def getUptime(hostname: String): Future[Int] =
      Future.successful(hosts.getOrElse(hostname, 0))
  }


  // Unit test - test its ability to sum values, regardless of where it is getting them from
  def testTotalUptime(): Unit = {
    val hosts = Map("host1" -> 10, "host2" -> 6)
    val client = new TestUptimeClient(hosts)
    val service = new UptimeService(client)
    val actual = service.getTotalUptime(hosts.keys.toList)
    val expected = hosts.values.sum
    //    assert(actual == expected)              // -> error as can't directly compare actual: Future[Int] (asynchronous) to expected: Int
  }

}
