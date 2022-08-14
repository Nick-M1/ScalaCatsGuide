package data_types.WriterDemo

/* The Writer[L, A] datatype represents a computation that produces a tuple containing a value of type L and one of type A:
   * Usually, the value L represents a description of the computation.
      A typical example of an L value could be a logging String and that's why from now on we will refer to it as the Logging side of the datatype.
   * The value A is the actual output of the computation.

   The main features that Writer provides are:
   - The flexibility regarding Log value management (It can be modified in multiple ways)
   - When two functions are composed together (e.g. using flatMap) the logs of both functions will be combined using an implicit Semigroup.

   Writer contains functions from Monad (flatMap etc.) and Semigroup (combining logs)


*/

import cats.Id
import cats.data.{Writer, WriterT}
import cats.instances.*

object Demo {

  // map() only affects the value, keeping the log side untouched
  val wrt1: WriterT[Id, String, Int] = Writer("map example", 1).map(_ + 1)
  val _1: (String, Int) = wrt1.run          // res: (String, Int) = ("map Example", 2) -> run unwraps the datatype, returning its contents


  // ap() applies a function, wrapped into a Writer
  val wrt2: WriterT[Id, String, Int] = Writer("ap value", 10)
  val wrt3: WriterT[Id, String, Int => Int] = Writer("ap function ", (i: Int) => i % 7)     // A here is a function
  wrt2.ap(wrt3).run         // res: (String, Int) = ("ap function ap value", 3) -> logs & values are combined


  // flatMap() applies a function (wrt5) to a Writer (wrt4)
  val wrt4: WriterT[Id, String, Int] = Writer("flatmap value ", 5)
  val wrt5: Int => WriterT[Id, String, Int] = (x: Int) => Writer("flatmap function ", x * x)      // basically a function for Writers
  wrt4.flatMap(wrt5).run   // res: (String, Int) = ("flatmap value flatmap function ", 25)

  // We can use the for comprehension to get same result as flatMap
  val wrt6: WriterT[Id, String, Int] = for {
    value <- Writer("flatmap value", 5)
    result <- wrt5(value)
  } yield result

  val _2: (String, Int) = wrt6.run      // res3: (String, Int) = ("flatmap value flatmap function ", 25)



  // EXTRA WRITER METHODS - to manager log side of computation:

  // tell : Append a value to the log side. It requires a Semigroup[L].
  val wrt7: WriterT[Id, String, Int] = Writer("tell example ", 1).tell("log append")
  val _3: (String, Int) = wrt7.run           // res: (String, Int) = ("tell example log append", 1)

  // swap : Exchange the two values of the Writer.
  val wrt8: WriterT[Id, String, String] = Writer("new value", "new log").swap
  val _4: (String, String) = wrt8.run        // res: (String, String) = ("new log", "new value")

  // reset : Delete the log side. It requires a Monoid[L] since it uses the empty value of the monoid.
  val wrt9: WriterT[Id, String, Int] = Writer("long log to discard", 42).reset
  val _5: (String, Int) = wrt9.run           // res: (String, Int) = ("", 42)

  // value : Returns only the value of the Writer
  val wrt10: Id[Int] = Writer("some log", 55).value     // 55

  // listen : Transform the value of the Writer to a tuple containing the current value and the current log.
  val wrt11: WriterT[Id, String, (Int, String)] = Writer("listen log", 10).listen
  val _6: (String, (Int, String)) = wrt11.run     // res: (String, (Int, String)) = ("listen log", (10, "listen log"))



}
