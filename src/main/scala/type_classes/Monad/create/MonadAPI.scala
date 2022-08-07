package type_classes.Monad.create

import type_classes.FlatMap.create.FlatMapCreate.FlatMap
import type_classes.Applicative.create.ApplicativeCreate.Applicative

object MonadAPI {

  trait Monad[F[_]] extends Applicative[F] with FlatMap[F] {
    // Inherits map(), flatMap() & pure()
  }


}
