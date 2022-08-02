package datatypes.EvalDemo

import cats.Eval
import cats.implicits.*

object RecursionDemo {

  /* Use Eval.defer to defer any computation that will return an Eval[A].
     Useful, because nesting a call to .value inside any of the Eval creation methods can be unsafe. */

  // EXAMPLE #1:

  // Recursion without Eval:
  def factorialNoEval(n: BigInt): BigInt = n match {
    case 1 => 1
    case _ => n * factorialNoEval(n - 1)
  }
  factorialNoEval(50000)                              // java.lang.StackOverflowError

  // Recursion with Eval
  def factorialEval(n: BigInt): Eval[BigInt] = n match {
    case 1 => Eval.now(1)
    case _ => Eval.defer(factorialEval(n - 1).map(_ * n))
  }
  factorialEval(50000).value                         // no error :)





  // EXAMPLE #2:

  // Without Eval - not stack-safe
  def foldRightNoEval[A, B](as: List[A], acc: B)(fn: (A, B) => B): B = as match {
    case Nil => acc
    case head :: tail => fn(head, foldRightNoEval(tail, acc)(fn))
  }


  // With Eval - Stack safe
  def foldRightEvalHelper[A, B](as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] = as match {
    case Nil => acc
    case head :: tail => Eval.defer(fn(head, foldRightEvalHelper(tail, acc)(fn)))
  }
  def foldRightEval[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    foldRightEvalHelper(as, Eval.now(acc))   { (a, b) => b.map(fn(a, _)) }  .value

  foldRightEval((1 to 100000).toList, 0L)(_ + _)  // res24: Long = 5000050000L
}
