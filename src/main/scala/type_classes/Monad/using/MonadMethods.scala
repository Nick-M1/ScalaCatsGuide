package type_classes.Monad.using

import cats.{Eval, Monad}
import cats.implicits.*

object MonadMethods extends App {

  /** ifElseM: <br>
   * Simulates an if/else-if/else in the context of an F. <br>
   * It evaluates conditions until one evaluates to true, and returns the associated F[A]. If no condition is true, returns els.
   * */
  Monad[Eval].ifElseM(
    Eval.later(false) -> Eval.later(1), Eval.later(true) -> Eval.later(2)     // if statements to evaluate
  )(
    Eval.later(5)                                                             // action if no if-statement returns true
  ).value
  // res: Int = 2

}
