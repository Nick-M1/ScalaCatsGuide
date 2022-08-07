package data_types.StateDemo

import cats.Eval
import cats.data.State
import cats.data.IndexedStateT

/* As we want to change between OPEN & CLOSE States, use IndexedStateT[F[_], SA, SB, A]
*
*  This data type models a stateful computation of the form SA => F[(SB, A)];
*  that's a function that receives an initial state of type SA and results in a state of
*   type SB and a result of type A, using an effect of F.
*
*  Starting State will be SA (Open in our case), which can only go to SB (close), then SA (open)...    */


object ChangingStates {

  sealed trait DoorState
  case object Open extends DoorState
  case object Closed extends DoorState

  case class Door(state: DoorState)

  def open: IndexedStateT[Eval, Closed.type, Open.type, Unit] = IndexedStateT.set(Open)
  def close: IndexedStateT[Eval, Open.type, Closed.type, Unit] = IndexedStateT.set(Closed)


  // TESTING:
  val valid: IndexedStateT[Eval, Closed.type, Open.type, Unit] = for {
    _ <- open
    _ <- close
    _ <- open
  } yield ()
  // cats.data.IndexedStateT@42676f89

  val invalid: IndexedStateT[Eval, Closed.type, Open.type, Unit] = for {
    _ <- open
//    _ <- open
  } yield ()
  // error: type mismatch;   -> closing door when it is already closed


//  valid.run(Open)     -> error as it is already open
  valid.run(Closed)






}
