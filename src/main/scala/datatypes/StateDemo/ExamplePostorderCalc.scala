package datatypes.StateDemo

import cats.data.{IndexedStateT, State}
import cats.data.State.*
import State._
import cats.syntax.applicative._
import cats.Eval

/* Implementation:
     Traverse elems from left to right, carrying a stack of operands with us as we go:
      • when we see a number, we push it onto the stack;
      • when we see an operator, we pop two operands off the stack, operate on them, and push the result in their place.

      1 2 + 3 *     // see 1, push onto stack
      2 + 3 *       // see 2, push onto stack
      + 3 *         // see +, pop 1 and 2 off of stack, push (1 + 2) = 3 in their place

      3 3 *         // see 3, push onto stack
      3 *           // see 3, push onto stack
      *             // see *, pop 3 and 3 off of stack, push (3 * 3) = 9 in their place


      So, 1 2 +  ->  1 + 2                                                      */


object ExamplePostorderCalc {

  type CalcState[A] = State[List[Int], A]             // type alias

  def evalSingle(sym: String): CalcState[Int] =       // Decision making for an individual char - is it a number of an operator?
    sym match {
      case "+" => operator(_ + _)
      case "-" => operator(_ - _)
      case "*" => operator(_ * _)
      case "/" => operator(_ / _)
      case num => operand(num.toInt)
    }

  def operand(num: Int): CalcState[Int] =                   // If char is operand/number -> add to stack
    State[List[Int], Int] ( stack => (num :: stack, num) )

  def operator(func: (Int, Int) => Int): CalcState[Int] =   // If char is operator -> take last 2 elems (numbers) off stack & apply this operator to them, then place this back on stack
    State[List[Int], Int] {
      case b :: a :: tail => {
        val ans = func(a, b)
        (ans :: tail, ans)
      }
      case _ => sys.error("Fail!")      // If at an operator & there arent 2 elems on stack -> error
    }


  // test for code so far - calcs "1 2 +":
  val calc1: IndexedStateT[Eval, List[Int], List[Int], Int] = for {
    _ <- evalSingle("1")
    _ <- evalSingle("2")
    ans <- evalSingle("+")
  } yield ans

  calc1.runA(Nil).value // res: Int = 3



  def evalAll(input: List[String]): CalcState[Int] =          // foreach elem in listOf-Chars/Numbers, apply evalSingle to it
    input.foldLeft(0.pure[CalcState]) { (a, b) =>
      a.flatMap(_ => evalSingle(b))
    }

  def evalInput(input: String): Int =                                 // Splits input str into list of individual chars/number-strs & runs the calc
    evalAll(input.split(" ").toList).runA(Nil).value


  // Test:
  evalInput("1 2 + 3 4 + *")      // res: Int = 21   = (1 + 2) * (3 + 4)



}
