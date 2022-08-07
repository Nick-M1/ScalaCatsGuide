package type_classes.Foldable.using

import cats.{Foldable, UnorderedFoldable}
import cats.instances.list.*
import cats.instances.int.*
import cats.instances.string.*
import cats.instances.vector.*
import cats.syntax.foldable.* // for combineAll and foldMap


/** Foldable abstracts the foldLeft and foldRight methods */
object FoldableUsing extends App {

  /** foldLeft from the standard library: <p>
      It needs an accumulator value & a binary function to combine it with each item in the sequence:      */
  List(1, 2, 3).foldLeft("nil")((accum, item) => s"$item then $accum")        // res: String = "3 then 2 then 1 then nil"


  /** USING CATS FOLDABLE:
   *
   *
   * Cats provides foldLeft & foldRight implementations for List, Vector, LazyList & Option
   *
   * They traverse through the list (via recursion) & apply the operation to the current elem and the accumulation of previous elems
   *
   *
   * foldLeft Vs foldRight: <br>
   *  * foldLeft starts traversing from left & foldRight from right of list. <br>
   *  * If the operator is associative (e.g. _+_ or _*_), then order doesn't matter so the result of foldLeft & foldRight will be the same. <br>
   *  * If the operator isn't associative (e.g. _-_ or _/_), then order does matter so the result of foldLeft & foldRight will be different. <br>
   *  * foldRight is STACK-SAFE as its implementation uses Eval, but foldLeft doesn't (so could get stack overflow) <p>
   *
   *  Using Foldable: <br>
   *  * Give the type of the object being folded over (& this object must be Foldable) <br>
   *  * As args in foldLeft/foldRight -> object being folded, the 1st value of the accumilator and the binary function
   *  */

  val a: List[Int] = List(1, 2, 3)
  Foldable[List].foldLeft(a, 0)(_ + _) // res: Int = 6

  Foldable[Option].foldLeft(Option(123), 10)(_ * _) // res: Int = 1230


  /** reduceLeftOption: <br>
   * Reduce the elements of this structure down to a single value by applying the provided aggregation function in a left-associative manner. <br>
   * Returns None if the structure is empty, otherwise the result of combining the cumulative left-associative result of the f operation over all of the elements.
   * 
   * reduceRightOption: <br>
   * Same as reduceLeftOption, but applies the provided aggregation function in a right-associative manner
   * 
   * */
  val l = List(6, 3, 2)
  
  Foldable[List].reduceLeftOption(l)(_ - _)                 // res: Option[Int] = Some(1) -> This is equivalent to (6 - 3) - 2
  Foldable[List].reduceLeftOption(List.empty[Int])(_ - _)   // res1: Option[Int] = None
  
  Foldable[List].reduceRightOption(l)((current, rest) => rest.map(current - _)).value                 // res: Option[Int] = Some(5) -> This is equivalent to 6 - (3 - 2)
  Foldable[List].reduceRightOption(List.empty[Int])((current, rest) => rest.map(current - _)).value   // res: Option[Int] = None
  
  
  /** isEmpty: <br>
   *  returns true if Foldable object is empty
   *
   *  nonEmpty: <br>
   *  returns false if Foldable object is empty
   *
   *  size: <br>
   *  returns number of elements in the object
   * */
  val b: Option[Int] = Option(42)
  Foldable[Option].nonEmpty(b)             // res: Boolean = true
  Foldable[Option].isEmpty(b)              // res: Boolean = false
  Foldable[Option].size(b)                 // res: Long = 1


  /** exists: <br>
   * Check whether at least one element satisfies the predicate. <br>
   *  If there are no elements, the result is false.
   *
   *
   * forall: <br>
   * Check whether all elements satisfy the predicate. <br>
   *  If there are no elements, the result is true.
   *
   * find: <br>
   * Find the first element matching the predicate, if one exists. <br>
   * returns an Option (will be None if the wrapper is empty)
   * */
  val c: List[Int] = List(1, 2, 3)

  Foldable[List].exists(c)(_ % 2 == 0)        // res: Boolean = true
  Foldable[List].forall(c)(_ % 2 == 0)        // res: Boolean = false
  
  Foldable[List].find(c)(_ % 2 == 0)          // res: Option[Int] = Some(2)

  
  /** count: <br>
   *  returns the number of elements in the structure that satisfy the given predicate
   *
   *  Note - this method is for UnorderedFoldable, which Foldable inherits from. Some structures (e.g. Map) is an UnorderedFoldable but not a Foldable.
   * */
  val map1: Map[Int, String] = Map[Int, String]()
  val map2: Map[Int, String] = Map(1 -> "hello", 2 -> "world", 3 -> "!")

  UnorderedFoldable[Map[Int, *]].count(map1)(_.nonEmpty)       // res: Long = 0
  UnorderedFoldable[Map[Int, *]].count(map2)(_.nonEmpty)       // res: Long = 2








  /** intercalate: <br>
   * Intercalate/insert an element between the existing elements while folding.
   * */
  Foldable[List].intercalate(List("a", "b", "c"), "-")        // res: String = a - b - c
  Foldable[List].intercalate(List("a"), "-")                  // res: String = a
  Foldable[List].intercalate(List.empty[String], "-")         // res: String = ""

  Foldable[Vector].intercalate(Vector(1, 2, 3), 1)            // res: Int = 8






  
  // TODO: FINISH
  Foldable[List].combineAll(List(1, 2, 3)) // res: Int = 6
  Foldable[List].foldMap(List(1, 2, 3))(_.toString) // res: String = "123"
  
  val ints: List[Vector[Int]] = List(Vector(1, 2, 3), Vector(4, 5, 6))
  (Foldable[List] compose Foldable[Vector]).combineAll(ints) // res: Int = 21

}
