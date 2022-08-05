package Case_Studies.MapReduce

import cats.Monoid
import cats.syntax.semigroup._ // for |+|

import cats.instances.int._ // for Monoid
import cats.instances.string._ // for Monoid
import cats.instances.future._ // for Applicative and Monad
import cats.instances.vector._ // for Foldable and Traverse

import cats.syntax.foldable._ // for combineAll and foldMap
import cats.syntax.traverse._ // for traverse

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/** In “big data”, MapReduce is a programming model for doing parallel data processing across clusters of machines (aka “nodes”). */


object Demo1 extends App {

  /** Parallelize map and fold: <p>

     Map transforms each individual element in a sequence independently, so can easily parallelize as there are no
      dependencies between the transformations applied to different elements <p>

     The parallel fold will yield the correct results if: <br>
      • we require the reducer function to be associative; <br>
      • we seed the computation with the identity of this function.
   */


  // Initial data -> Map using func() -> Fold/reduce   (Single-threaded version):
  def foldMap[A, B: Monoid](as: Vector[A])(func: A => B): B =
    as.foldLeft(Monoid[B].empty)(_ |+| func(_))           // apply func() to each new element in traversal

  foldMap(Vector(1, 2, 3))(identity)                        // res: Int = 6
  foldMap(Vector(1, 2, 3))(_.toString + "! ")               // res: String = "1! 2! 3! "
  foldMap("Hello world!".toVector)(_.toString.toUpperCase)  // res: String = "HELLO WORLD!"
  

  /** Multi‐CPU implementation that simulates the distribute work in a map‐reduce cluster: <br>
      1. Start with an initial list of all the data we need to process; <br>
      2. Divide the data into batches, sending one batch to each CPU; <br>
      3. CPUs run a batch‐level map phase in parallel; <br>
      4. CPUs run a batch‐level reduce phase in parallel, producing a local result for each batch; <br>
      5. Reduce/combine the results for each batch to a single final result.
  */

  def parallelFoldMap[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = {
    val numCores = Runtime.getRuntime.availableProcessors         // Calc number of available CPU cores (8)
    val groupSize = (1.0 * values.size / numCores).ceil.toInt     // Split workload to each core equally
    values
      .grouped(groupSize)                               // group by each core's workload
      .toVector
      .traverse(group => Future(group.foldMap(func)))  // Give the work to each core
      .map(_.combineAll)                               // Once work done by each core (in parallel, combine it together)
  }

  val future: Future[Int] = parallelFoldMap((1 to 1000).toVector)(_ * 1000)
  Await.result(future, 1.second)                      // res18: Int = 500500000


}
