package Case_Studies.GCounter

import cats.kernel.CommutativeMonoid

import cats.instances.int._ // for Monoid
import cats.instances.list._ // for Monoid
import cats.instances.map._ // for Monoid
import cats.syntax.semigroup._ // for |+|
import cats.syntax.foldable._ // for combineAll

/* Use Cats for Abstract types (so any type could be stored, not just String & Int)

     3 operations:
      • addition (in increment and total);
      • maximum (in merge);
      • the identity element 0 (in increment and merge).

     Monoids satisfy 2 laws:
      - Binary operation (+) must be associative -> (a + b) + c == a + (b + c)
      - The empty element must be an identity -> 0 + a == a + 0 == a


     Increment Operation:
     Need an identity to initialise the counter.
     Rely on associativity to ensure the specific sequence of merges gives the correct value.

     Total Operation:
     Implicitly rely on associativity and commutativity to ensure we get the correct value no matter what arbitrary order we choose to sum the
       per‐machine counters.
     Implicitly assume an identity, which allows us to skip machines for which we do not store a counter.

     Merge Operation:
     Rely on commutativity to ensure that machine A merging with machine B yields the same result as machine B merging with machine A.
     Associativity to ensure we obtain the correct result when three or more machines are merging data.
     Identity element to initialise empty counters.
     Idempotency, to ensure that if two machines hold the same data in a per‐machine counter, merging data will not lead to an incorrect result.


     *Commutative operations are ones that merge two data instances, with a result that captures all the information in both instances.
     *Idempotent operations are ones that return the same result again and again if they are executed multiple times.
       Formally, a binary operation max is idempotent if the following relationship holds:  a max a = a



     Method       Identity   Commutative   Associative   Idempotent
     increment    Y          N             Y             N
     merge        Y          Y             Y             Y
     total        Y          Y             Y             N


     From this we can see that:
      • increment requires a monoid;
      • total requires a commutative monoid; and
      • merge required an idempotent commutative monoid, also called abounded semilattice.

     Since increment and get both use the same binary operation (addition) it’s usual to require the same commutative monoid for both.




     With these properties, can use any data-type / structure with operations satisfying these properties.
     Previously, we used numbers (Int) but we can also use a set.

     For a set:
      - Binary operation being union
      - Identity element the empty set
     With this simple substitution of Int for Set[A] we can create a GSet type.

  */

object Demo2 {

  // Cats has an implementation for Cummutative Monoid but not Bounded Semi-lattice, so implement our own
  object wrapper {

    // Abstract/Generic type-class (need to implement for each type that uses it)
    trait BoundedSemiLattice[A] extends CommutativeMonoid[A] {
      def combine(a1: A, a2: A): A

      def empty: A
    }

    object BoundedSemiLattice {
      // Integer implementation
      implicit val intInstance: BoundedSemiLattice[Int] = new BoundedSemiLattice[Int] {
        def combine(a1: Int, a2: Int): Int =
          a1 max a2

        val empty: Int =
          0
      }

      // Set implementation
      implicit def setInstance[A](): BoundedSemiLattice[Set[A]] = new BoundedSemiLattice[Set[A]] {
        def combine(a1: Set[A], a2: Set[A]): Set[A] =
          a1 union a2

        val empty: Set[A] =
          Set.empty[A]
      }
    }
  };

  import wrapper._ // Good practise to put our new type in a new code-page & import it


  // Generic type-class of GCounter (using key-value stores) - can be used for more concrete implementations like a Map or a relational database (anything that stores key-value pairs)
  trait GCounter[F[_, _], K, V] {
    def increment(f: F[K, V])(k: K, v: V)(implicit m: CommutativeMonoid[V]): F[K, V]

    def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V]

    def total(f: F[K, V])(implicit m: CommutativeMonoid[V]): V
  }

  object GCounter {
    def apply[F[_, _], K, V](implicit counter: GCounter[F, K, V]): GCounter[F, K, V] =
      counter
  }


  // Implementation of GCounter type-class trait, but for a Map (will have multiple of these for different implementations, e.g. to a DB)
  implicit def mapGCounterInstance[K, V]: GCounter[Map, K, V] =
    new GCounter[Map, K, V] {
      def increment(map: Map[K, V])(key: K, value: V)(implicit m: CommutativeMonoid[V]): Map[K, V] = {
        val total = map.getOrElse(key, m.empty) |+| value
        map + (key -> total)
      }

      def merge(map1: Map[K, V], map2: Map[K, V])(implicit b: BoundedSemiLattice[V]): Map[K, V] =
        map1 |+| map2

      def total(map: Map[K, V])(implicit m: CommutativeMonoid[V]): V =
        map.values.toList.combineAll
    }


  // Testing:
  val g1 = Map("a" -> 7, "b" -> 3)
  val g2 = Map("a" -> 2, "b" -> 5)
  val counter = GCounter[Map, String, Int]

  val merged = counter.merge(g1, g2) // merged: Map[String, Int] = Map("a" -> 7, "b" -> 5)
  val total = counter.total(merged) // total: Int = 12


  // type-class for a Key-value store
  trait KeyValueStore[F[_, _]] {
    def put[K, V](f: F[K, V])(k: K, v: V): F[K, V]

    def get[K, V](f: F[K, V])(k: K): Option[V]

    def getOrElse[K, V](f: F[K, V])(k: K, default: V): V =
      get(f)(k).getOrElse(default)

    def values[K, V](f: F[K, V]): List[V]
  }

  // Implementation of KeyValueStore, but for type Map
  implicit val mapKeyValueStoreInstance: KeyValueStore[Map] = new KeyValueStore[Map] {
    def put[K, V](f: Map[K, V])(k: K, v: V): Map[K, V] =
      f + (k -> v)

    def get[K, V](f: Map[K, V])(k: K): Option[V] =
      f.get(k)

    override def getOrElse[K, V](f: Map[K, V])(k: K, default: V): V =
      f.getOrElse(k, default)

    def values[K, V](f: Map[K, V]): List[V] =
      f.values.toList
  }

  // ???
  // Implement syntax to enhance data types for which we have instances:
  implicit class KvsOps[F[_, _], K, V](f: F[K, V]) {
    def put(key: K, value: V)(implicit kvs: KeyValueStore[F]): F[K, V] =
      kvs.put(f)(key, value)

    def get(key: K)(implicit kvs: KeyValueStore[F]): Option[V] =
      kvs.get(f)(key)

    def getOrElse(key: K, default: V)(implicit kvs: KeyValueStore[F]): V =
      kvs.getOrElse(f)(key, default)

    def values(implicit kvs: KeyValueStore[F]): List[V] =
      kvs.values(f)
  }


  // Can generate GCounter instances for any data type that has instances of KeyValueStore and CommutativeMonoid using an implicit def
  implicit def gcounterInstance[F[_, _], K, V](implicit kvs: KeyValueStore[F], km: CommutativeMonoid[F[K, V]]): GCounter[F, K, V] = new GCounter[F, K, V] {
    def increment(f: F[K, V])(key: K, value: V)(implicit m: CommutativeMonoid[V]): F[K, V] = {
      val total = f.getOrElse(key, m.empty) |+| value
      f.put(key, total)
    }

    def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V] =
      f1 |+| f2

    def total(f: F[K, V])(implicit m: CommutativeMonoid[V]): V =
      f.values.combineAll
  }




















  //  // GCounter system - Same as before but using our nre abstract types
  //  final case class GCounter[A](counters: Map[String,A]) {
  //    def increment(machine: String, amount: A)(implicit m: CommutativeMonoid[A]): GCounter[A] = {
  //      val value = amount |+| counters.getOrElse(machine, m.empty)
  //      GCounter(counters + (machine -> value))
  //    }
  //
  //    def merge(that: GCounter[A])(implicit b: BoundedSemiLattice[A]): GCounter[A] =
  //      GCounter(this.counters |+| that.counters)
  //
  //    def total(implicit m: CommutativeMonoid[A]): A =
  //      this.counters.values.toList.combineAll
  //  }


}
