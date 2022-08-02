package type_class_definitions

import ApplicativeCreate.Applicative
import FlatMapCreate.FlatMap

object MonadCreate {

  trait Monad[F[_]] extends Applicative[F] with FlatMap[F]{
    // Inherits map(), flatMap() & pure()
  }
  
  
  
}
