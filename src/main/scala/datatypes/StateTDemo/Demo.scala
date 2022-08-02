package datatypes.StateTDemo

import cats.data.StateT
import cats.Eval

/* StateT[F[_], S, A] is a Monad transformer of State.

   Because StateT is defined in terms of F, it is a monad only if F is a monad.
   Additionally, StateT may acquire new capabilities via F:
     for example, if F is capable of error handling via MonadThrow[F], then Cats derives an instance of MonadThrow[StateT[F, S, *]].

   The type parameters are:
   - F[_] represents the effect in which the computation is performed.
   - S represents the underlying state, shared between each step of the state machine.
   - A represents the return value.

   It can be seen as a way to add the capability to manipulate a shared state to an existing computation in the context of F.    */



object Demo {
  // Relationship between State & StateT:
  type myState[S, A] = StateT[Eval, S, A]     // State is just StateT with Eval as the effect F
  
  /* Therefore, StateT exposes the same methods of State, such as: modify, get and set
     Plus additional methods, that handles effectful computations, eg: modifyF, setF and liftF      */
  
  
}
