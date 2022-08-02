package datatypes.StateDemo

import cats.data.{IndexedStateT, State}
import cats.data.State.*
import State._
import cats.Eval

/* Allows us to pass additional state around as part of a computation.
   Can model mutable state in a purely functional way, without using actual mutation. */

object Demo1 extends App {

  val step1: State[Int, String] = State[Int, String]{ num =>
    val ans = num + 1
    (ans, s"Result of step1: $ans")
  }
  val step2: State[Int, String] = State[Int, String]{ num =>
    val ans = num * 2
    (ans, s"Result of step2: $ans")
  }

  val both: IndexedStateT[Eval, Int, Int, (String, String)] = for {
    a <- step1
    b <- step2
  } yield (a, b)

  val (state, result) = both.run(20).value
  // state: Int = 42   // result: (String, String) = ("Result of step1: 21", "Result of step2 : 42")



  // Methods on State Monad:

  // - get extracts the state as the result;
  State.get[Int]
    .run(10).value          // res1: (Int, Int) = (10, 10)

  // - set updates the state and returns unit as the result;
  State.set[Int](30)
    .run(10).value          // res2: (Int, Unit) = (30, ())

  // - pure ignores the state and returns a supplied result;
  State.pure[Int, String]("Result")
    .run(10).value         // res3: (Int, String) = (10, "Result")

  // - inspect extracts the state via a transformation function;
  State.inspect[Int, String](x => s"$x!")
    .run(10).value        // res4: (Int, String) = (10, "10!")

  // - modify updates the state using an update function.
  State.modify[Int](_ + 1)
    .run(10).value        // res5: (Int, Unit) = (11, ())




  val program: State[Int, (Int, Int, Int)] = for {
    a <- get[Int]
    _ <- set[Int](a + 1)
    b <- get[Int]
    _ <- modify[Int](_ + 1)
    c <- inspect[Int, Int](_ * 1000)
  } yield (a, b, c)

  program.run(1).value          // res: (Int, (Int, Int, Int)) = (3, (1, 2, 3000))


}
