package type_classes.FlatMap.using

import cats.{Eval, FlatMap}
import cats.implicits._

object FlatMapUsing extends App {

  /** flatten: <p>
   * "flatten" a nested F of F structure into a single-layer F structure.
   * */
  val nested1: Eval[Eval[Int]] = Eval.now(Eval.now(3))
  val flattened: Eval[Int] = nested1.flatten
  flattened.value                   // res: Int = 3

  val nested2 = List(List(1, 2, 3), List(4, 5, 6), List(7))
  nested2.flatten                   // res: List[Int] = List(1, 2, 3, 4, 5, 6, 7)


  /** productREval: <br>
   *  Sequentially compose two actions, discarding any value produced by the first. This variant of productR also lets you define the evaluation strategy of the second action. For instance you can evaluate it only after the first action has finished: <p>
   *
   *  productLEval: <br>
   *  Same as productREval, but instead discards any value produced by the second action
   * */
  val a: Option[Int] = Some(3)
  def b: Option[String] = Some("foo")

  a.productREval(Eval.later(b))         // res: Option[String] = Some(foo)
  a.productLEval(Eval.later(b))         // res: Option[Int] = Some(3)


  /** flatTap: <p>
   *  Apply a monadic function and discard the result while keeping the effect.
   * */
  def nCats(n: Int) = List.fill(n)("cat")

  Option(1).flatTap(_ => None)          // res: Option[Int] = None
  Option(1).flatTap(_ => Some("123"))   // res: Option[Int] = Some(1)

  List[Int](0).flatTap(nCats)           // res: List[Int] = List()
  List[Int](4).flatTap(nCats)           // res: List[Int] = List(4, 4, 4, 4)


  /** foreverM: <p>
   * Like an infinite loop of >> calls. This is most useful effect loops that you want to run forever in for instance a server.
   *  This will be an infinite loop, or it will return an F[Nothing].
   * */
  // TODO: Find example...


}
