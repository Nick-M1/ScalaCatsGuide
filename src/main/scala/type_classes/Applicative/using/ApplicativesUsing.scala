package type_classes.Applicative.using

import cats.Applicative
import cats.data.State
import cats.instances.list.*
import cats.instances.option.*
import cats.syntax.applicative.*
import cats.implicits.*


object ApplicativesUsing extends App {

  // METHODS:

  /** pure: <p>
   * lifts/wraps any value into the Applicative Functor
   * E.g.  Int -> List[Int],   String -> Option[String]
   * */
  val int1 = 2
  Applicative[List].pure(int1)     // List(2)
  Applicative[Option].pure(int1)   // Some(2)

  // Pure extension method
  int1.pure[List]                  // List(2)
  int1.pure[Option]                // Some(2)



  /** replicateA: <p>
   *  Given fa and n, apply fa n times to construct an F[List[A]] value.
   * */
  type Counter[A] = State[Int, A]
  val getAndIncrement: Counter[Int] = State { i => (i + 1, i) }
  val getAndIncrement5: Counter[List[Int]] = Applicative[Counter].replicateA(5, getAndIncrement)
  getAndIncrement5.run(0).value           // res: (Int, List[Int]) = (5, List(0, 1, 2, 3, 4))

  Applicative[Option].replicateA(5, Option("HELLO"))      // res: Option[List[String]] = Some(List(HELLO, HELLO, HELLO, HELLO, HELLO))



  /** unlessA: <br>
   * Returns the given argument (mapped to Unit) if cond is false, otherwise, unit lifted into F. <p>
   *
   * whenA: <br>
   * Returns the given argument (mapped to Unit) if cond is true, otherwise, unit lifted into F.
   * */
  val fullList = List(1, 2, 3)
  val emptyList = List.empty[Int]

  Applicative[List].unlessA(true)(fullList)      // res: List[Unit] = List(())
  Applicative[List].unlessA(false)(fullList)     // res: List[Unit] = List((), (), ())

  Applicative[List].unlessA(true)(emptyList)     // res: List[Unit] = List(())
  Applicative[List].unlessA(false)(emptyList)    // res: List[Unit] = List()


  Applicative[List].whenA(true)(fullList)        // res: List[Unit] = List((), (), ())
  Applicative[List].whenA(false)(fullList)       // res: List[Unit] = List(())

  Applicative[List].whenA(true)(emptyList)       // res: List[Unit] = List()
  Applicative[List].whenA(false)(emptyList)      // res: List[Unit] = List(())




  // TODO: This is a method for Apply...
  println(
    Applicative[Option].map3(Some(5), Some(6), Some(7))(_ + _ + _)
  )
  //  Applicative[Option].map3(Some(5), None, Some(7))(_ + _ + _)


}
