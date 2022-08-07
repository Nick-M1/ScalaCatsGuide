package type_classes.Functor.using

import cats.Functor
import cats.implicits.*

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Queue

object FunctorUsing extends App {

  // METHODS
  
  /** compose: */
  val l = List(Some(1), None, Some(2))      // List[Option[Int]]
  Functor[List].compose[Option].map(l)(_ + 1)        // res: List[Option[Int]] = List(Some(2), None, Some(3))
  

  /** fMap: <p>
   * Alias for map, since map can't be injected as syntax if the implementing type already had a built-in .map method.
   * */
  val m: Map[Int, String] = Map(1 -> "hi", 2 -> "there", 3 -> "you")
  m.fmap(_ ++ "!")              // res: Map[Int, String] = Map(1 -> hi !, 2 -> there !, 3 -> you !)


  /** widen: <p>
   * Lifts natural subtyping covariance of covariant Functors. <p>
   * Implemented via a type-cast, so may throw a ClassCastException
   * */

  // ?????

  /** lift: <p>
   * Lift a function f to operate on Functors
   * */
  val o = Option(42)
  val liftedFunc = Functor[Option].lift((x: Int) => x + 10)     // lifts func (x => x+10) to accept a functor as an arg for x
  liftedFunc(o)             // res: Option[Int] = Some(52)


  /** void: <p>
   * Empty the fa of the values, preserving the structure
   * */
  val p = List(1, 2, 3)
  Functor[List].void(p)         // res: List[Unit] = List((), (), ())


  /** fproduct & fproductLeft: <p>
   * Tuple the values in fa with the result of applying a function with the value <br>
   * fproduct returns tuple     (input, func_output) <br>
   * fproductLeft returns tuple (func_output, input)
   * */
  val q = Option(42)
  Functor[Option].fproduct(q)(x => (x+1).toString)          // res: Option[(String, Int)] = Some((42, 42))
  Functor[Option].fproductLeft(q)(x => (x+1).toString)      // res: Option[(String, Int)] = Some((43, 43))


  /** tupleLeft & tupleRight: <p>
   *  Tuples the A value in F[A] with the supplied B value <br>
   *  tupleLeft -> the B value on the left <br>
   *  tupleRight -> the B value on the right
   * */
  val r = Queue("hello", "world")
  Functor[Queue].tupleLeft(r, 42)           // res: scala.collection.immutable.Queue[(Int, String)] = Queue((42, hello), (42, world))
  Functor[Queue].tupleRight(r, 42)          // res: scala.collection.immutable.Queue[(String, Int)] = Queue((hello, 42), (world, 42))
  
  
  /** unzip: <p>
   * Un-zips an F[(A, B)] consisting of element pairs or Tuple2 into two separate F's tupled.
   * */
  val s = List((1, 2), (3, 4))
  Functor[List].unzip(s)                // res: (List[Int], List[Int]) = (List(1, 3), List(2, 4))
  
}
