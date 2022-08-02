package datatypes.EvalDemo

import cats.Eval
import cats.implicits.*

/**    CATS EVAL MONAD
 *     Scala:    | Cats:    | Properties:
 *     val       | Now      | eager, memoized
 *     def       | Always   | lazy, not memoized
 *     lazy val  | Later    | lazy, memoized
 */


object Demo1 extends App {

  // EAGER EVALUATION - evaluates expression straight away (when it is defined)
  val eager: Eval[Int] = Eval.now {
    println("Running expensive calculation...")
    1 + 2 * 3
  }
  // Running expensive calculation...
  // eager: Eval[Int] = Now(7)

  eager.value   // Now(7)  -> Can get the result of the expression (but the expression won't be recalculated (will always be the same result)



  // LAZY & MEMOIZED EVALUATION - Only evaluates expression when it is 1st called (not when it is defined)
  val lazyEval: Eval[Int] = Eval.later {
    println("Running expensive calculation...")
    1 + 2 * 3
  }
  // lazyEval: Eval[Int] = cats.Later@11f3030d

  lazyEval.value
  // Running expensive calculation...
  // res1: Int = 7

  lazyEval.value            // Expression already calculated (when it was 1st called), so it uses the same value as before (so lazy vals won't change result
  // res2: Int = 7




  // LAZY & NON-MEMOIZED EVALUATION - Evaluates expression each time it is called, and not when defined
  val always: Eval[Int] = Eval.always {
    println("Running expensive calculation...")
    1 + 2 * 3
  }
  // always: Eval[Int] = cats.Always@3e106705

  always.value
  // Running expensive calculation...
  // res3: Int = 7

  always.value                          // Expression called 2nd time gets re-evaluated (not memoized)
  // Running expensive calculation...
  // res4: Int = 7




  // CHAINING LAZY COMPUTATIONS:
  /* Can chain together Eval computations in a stack-safe way (e.g. foldRight method found in Foldable & performing mutual tail-recursive calls)
  *  Because Eval guarantees stack-safety, can chain a lot of computations together using flatMap without stack-overflow.                         */

  def even(n: Int): Eval[Boolean] =
    Eval.always(n == 0).flatMap {
      case true => Eval.True
      case false => odd(n - 1)
    }

  def odd(n: Int): Eval[Boolean] =
    Eval.always(n == 0).flatMap {
      case true => Eval.False
      case false => even(n - 1)
    }

  odd(199999).value       // true


  
  
  
  
  
  


}
