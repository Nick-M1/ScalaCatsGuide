package using_type_classes

import cats.Monoid

object SemigroupUsing2 extends App {

  import cats.Semigroup
  // User-defined implementation of Semigroup for Int
  given Semigroup[Int] with {
    def combine(x: Int, y: Int): Int = x + y
  }

  Semigroup[Int].combine(1, 2)                              // 3
  Semigroup[Int].combine(1, Semigroup[Int].combine(2, 3))   // 6
  Semigroup[Int].combine(Semigroup[Int].combine(1, 2), 3)   // 6




  import cats.instances._
  import cats.syntax.semigroup._

  5 |+| 10                                      // 15
  "Hi " |+| "there" |+| Monoid[String].empty    // "Hi there"
  ("hello", 10) |+| (" world", 15)              // ("hello world", 25)  -> = (tuple1._1 ++ tuple2._2,  tuple1._1 + tuple2._2)

  Map("hello" -> 1, "world" -> 1) |+| Map("hello" -> 2, "cats"  -> 3)
    // Map("world" -> 1, "hello" -> 3, "cats"  -> 3)
    // "hello" appears in both Maps, so it's values get added together


  // EXAMPLE INSTANCES that Cats has built-in from
  import cats.instances._
  Semigroup[Int]            // cats.kernel.instances.IntGroup@56f76801
  Semigroup[String]         // cats.kernel.instances.StringMonoid@aae1ef2

  Semigroup[List[Byte]]     // cats.kernel.instances.ListMonoid@2d02d52b
  Semigroup[Set[Int]]       // cats.kernel.instances.SetSemilattice@16ee486b


  // EXAMPLE - MERGING MAPS
  def optionCombine[A: Semigroup](a: A, opt: Option[A]): A =
    opt.map(a |+| _).getOrElse(a)

  def mergeMap[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
    lhs.foldLeft(rhs) {
      case (acc, (k, v)) => acc.updated(k, optionCombine(v, acc.get(k)))
    }

  // Test:
  mergeMap(
    Map('a' -> 1, 'b' -> 2),
    Map('b' -> 3, 'c' -> 4)
  )     // Map[Char, Int] = Map('b' -> 5, 'c' -> 4, 'a' -> 1)

  mergeMap(
    Map(1 -> List("hello")),
    Map(2 -> List("cats"), 1 -> List("world"))
  )     // Map[Int, List[String]] = Map(2 -> List("cats"), 1 -> List("hello", "world"))


}
