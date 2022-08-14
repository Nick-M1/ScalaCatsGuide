package Starter

object TypeClass_Basics2 {

  /**  THE PROBLEM: <br>
      When working with generics, we might need specialised implementations for our generic functions <p>

      With the function below, we could have a List of ints, Strings, Options, Futures ... <br>
      Each of these implementations will have the same 'function structure (similar input and output types), <br>
        but the actual implentation will be different, e.g.: <br>
        * For a List of ints -> sum the ints together to a single Int <br>
        * For a List of Strings -> Concatenate the Strings together to a single String
  */

  def processMyList1[T](list: List[T]): T = ???  // aggregate a list


  /**  CATS SOLUTION: */

  // Type-class (an abstract / generic definition of the method/methods):
  trait Summable[T] {
    def sumElements(list: List[T]): T
  }

  
  // Implementations for each specific type A could be:
  given Summable[Int] with {               // List of Ints
    def sumElements(list: List[Int]): Int = list.sum
  }

  given Summable[String] with {         // List of Strings
    def sumElements(list: List[String]): String = list.mkString("")
  }


  // Our method rewritten below (1# & 2# work the exact same way, just different syntax):
  // The implicit and the context bound both act as a capability enhancer (adding additional functionality/methods)
  //   and as a type-constraint (if compiler can't find an implicit implementation for that type (e.g. List[Boolean]) then compiler error)

  // #1 - Using Implicits / Given-Using   (Easier for user-defined type-classes)      -> NOTE: Given/Using is for Scala 3, Implicits is for Scala 2
  def processMyList2[T](list: List[T])(using summable: Summable[T]): T =
    summable.sumElements(list)

  // #2a - Using Context Bounds  (Easier for Cat's built-in type-classes
  def processMyList3[T: Summable](list: List[T]): T =
    implicitly[Summable[T]].sumElements(list)
//    val summable = implicitly[Summable[T]]             // Could extract the implicitly summable to its own val if used multiple times in func
//    summable.sumElements(list)


  // #2b - Using Context Bounds, but easier syntax with a companion object
  object Summable {
    def apply[A](using e: Summable[A]): Summable[A] = e
  }

  def processMyList4[T: Summable](list: List[T]): T = {
    Summable[T].sumElements(list)
  }


  // Testing our code:
  def main(args: Array[String]): Unit = {
    processMyList4(List(1,2,3))                         // 6
    processMyList4(List("Scala ", "is ", "awesome"))    // "Scala is awesome"
//    processMyList2(List(true, true, false))           // Error: List[Boolean] not implemented
  }

}


/** Overview:
*   The behaviour we have implemented is called “ad hoc polymorphism” because the sumElements ability is unlocked only in the presence of an
*     implicit instance of the trait which provides the method definition, right there when it’s called, hence the “ad hoc” name.
*     “Polymorphism” because the implementations we can provide can obviously be different for different types, as we did with Int and String.
*
*   The trait Summable[T] (the type-class) is just a template / abstraction of what we want to do for any implementation
*   Combine it with one/more implicit instances of the trait (e.g. IntSummable & StringSummable) to create specific implementations for certain types
*     and not for others, in this “ad hoc polymorphic” style.
*                                                                                                                                                 */
