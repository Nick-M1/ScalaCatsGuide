package datatypes.WriterDemo

import cats.data.Writer
import cats.syntax.compose._
import scala.math.sqrt

object ExampleLogging {

  // Performing Math operations & logging the result
  val writer1: Writer[String, Double]           = Writer.value(5.0).tell("Initial value ")
  val writer2: Writer[String, Double => Double] = Writer("sqrt ", (i: Double) => sqrt(i))
  val writer3: Double => Writer[String, Double] = (x: Double) => Writer("add 1 ", x + 1)
  val writer4: Writer[String, Double => Double] = Writer("divided by 2 ", (x: Double) => x / 2)

  val writer5: Writer[String, Double => Double] = Writer[String, Double => Double](writer3(0).written,(x: Double) => writer3(x).value)


  // Running - By chain operations:
  writer1
    .ap(writer2)
    .flatMap(writer3(_))
    .ap(writer4)
    .map(_.toString)
    .run
  // res: (String, String) = ( "divided by 2 sqrt Initial value add 1 ", "1.618033988749895" )

  // Running - By for-comprehension
  (
    for {
      initialValue <- writer1
      sqrt <- writer2
      addOne <- writer5
      divideBy2 <- writer4
    } yield (sqrt >>> addOne >>> divideBy2)(initialValue)
  ).run
  // res: (String, Double) = (  "Initial value sqrt add 1 divided by 2 ", 1.618033988749895 )
}
