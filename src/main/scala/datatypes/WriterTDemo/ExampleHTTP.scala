package datatypes.WriterTDemo

import cats.data.WriterT
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/* For a app that pings multiple HTTP services and collects the time spent in each call,
    returning the total time of the whole execution at the end. Simulate the calls by successful Future values.

   Using WriterT to log each step of our application, compute, the time and work within the Future effect. */

object ExampleHTTP extends App {

  // Mocked HTTP calls
  def pingService1() : Future[Int] = Future.successful(100)
  def pingService2() : Future[Int] = Future.successful(200)
  def pingService3() : Future[Int] = Future.successful(50)
  def pingService4() : Future[Int] = Future.successful(75)

  // Returns WriterT containing (ping to service #1 took 100" , 100) for pingService1
  def pingToWriterT(ping: Future[Int], serviceName: String) : WriterT[Future, String, Int] =
    WriterT
      .valueT[Future, String, Int](ping)
      .tell(s"ping to $serviceName ")
      .flatMap(pingTime => WriterT.put(pingTime)(s"took $pingTime \n"))

  val resultWriterT: WriterT[Future, String, Int] = for {
    ping1 <- pingToWriterT(pingService1(), "service #1")      // flatMap only returns the value to ping1 as Int
    ping2 <- pingToWriterT(pingService2(), "service #2")
    ping3 <- pingToWriterT(pingService3(), "service #3")
    ping4 <- pingToWriterT(pingService4(), "service #4")
  } yield ping1 + ping2 + ping3 + ping4


  // Calc total time at end
  val resultFuture: Future[String] = resultWriterT.run.map {
    case (log: String, totalTime: Int) => s"$log> Total time: $totalTime"
  }



  // TESTING:
  Await.result(resultFuture, Duration.Inf)
  // res11: String = """ping to service #1 took 100
  // ping to service #2 took 200
  // ping to service #3 took 50
  // ping to service #4 took 75
  // > Total time: 425"""


  println(pingToWriterT(pingService1(), "service #1"))

}
