package type_classes.Traverse.using

import cats.Traverse
import cats.implicits._
import java.io.IOException

object TraverseMethods extends App {

  /** traverse: <p>
   * Given a function which returns a G effect, thread this effect through the running of this function on all the values in F, returning an F[B] in a G context. */
  def parseInt(s: String): Option[Int] =
    Either.catchOnly[NumberFormatException](s.toInt).toOption

  List("1", "2", "3").traverse(parseInt)      // res: Option[List[Int]] = Some(List(1, 2, 3))
  List("1", "two", "3").traverse(parseInt)    // res: Option[List[Int]] = None -> error was thrown & caught from parseInt


  /** traverseTap: <p>
   * Given a function which returns a G effect, thread this effect through the running of this function on all the values in F, returning an F[A] in a G context, ignoring the values returned by provided function.
   * */
  def debug(msg: String): Either[IOException, Unit] = Right(())

  List("1", "2", "3").traverseTap(debug)        // res: Either[IOException, List[String]] = Right(List(1, 2, 3))
  List("1", "two", "3").traverseTap(parseInt)   // res: Either[IOException, List[String]] = None -> error was thrown & caught from parseInt


  /** flatTraverse: <p>
   * A traverse followed by flattening the inner result.
   * */
  val a = Option(List("1", "two", "3"))

  a.traverse(_.map(parseInt))        // res: List[Option[Option[Int]]] = List(Some(Some(1)), Some(None), Some(Some(3))) -> without flatTraverse
  a.flatTraverse(_.map(parseInt))    // res: List[Option[Int]] = List(Some(1), None, Some(3))


  /** sequence: <p>
   *  Thread all the G effects through the F structure to invert the structure from F[G[A]] to G[F[A]]
   * */
  val b: List[Option[Int]] = List(Some(1), Some(2))
  val c: List[Option[Int]] = List(None, Some(2))
  b.sequence                        // res: Option[List[Int]] = Some(List(1, 2))
  c.sequence                        // res: Option[List[Int]] = None


  /** flatSequence: <p>
   * Thread all the G effects through the F structure and flatten to invert the structure from <br> F[G[F[A]]] to G[F[A]].
   *  */
  val d: List[Option[List[Int]]] = List(Some(List(1, 2)), Some(List(3)))
  val e: List[Option[List[Int]]] = List(None, Some(List(3)))
  d.flatSequence                    // res: Option[List[Int]] = Some(List(1, 2, 3))
  e.flatSequence                    // res: Option[List[Int]] = None


  /** mapWithIndex: <p>
   * Same as map, but also provides the value's index in structure F when calling the function.
   * */
  val f = List("H", "E", "L", "L", "O")
  def func1(char: String, idx: Int): String = idx.toString + ": " + char

  f.mapWithIndex(func1)             // res: List[String] = List("0: H", "1: E", "2: L", "3: L", "4: O")


  /** zipWithIndex: <p>
   * Traverses through the structure F, pairing the values with assigned indices. <br>
   * The behavior is consistent with the Scala collection library's zipWithIndex for collections such as List <p>
   * Uses mapWithIndex
   * */
  val g = List("H", "E", "L", "L", "O")
  g.zipWithIndex                    // res: List[(String, Int)] = List(("H",0), ("E",1), ("L",2), ("L",3), ("O",4))
}
