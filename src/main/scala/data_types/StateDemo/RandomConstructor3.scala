package data_types.StateDemo

// TODO: WIP !!!!!!!!!!!!

import cats.data.State
import cats.data.StateT
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/* Interleaving Effects:
*  Making function asynchronous (e.g. Random number generator fetches results asynchronously from a server.
*
* */

object RandomConstructor3 extends App {

  final case class Robot(id: Long, sentient: Boolean, name: String, model: String)

  final case class AsyncSeed(long: Long) {
    def next: Future[AsyncSeed] = Future(AsyncSeed(long * 6364136223846793005L + 1442695040888963407L))
  }

  // To get the next number or boolean:
  // State[S, A] is an alias for StateT[Eval, S, A] - a monad transformer defined as StateT[F[_], S, A]
  val nextLong: StateT[Future, AsyncSeed, Long] = StateT { seed => seed.next zip Future.successful(seed.long) }
  val nextBoolean: StateT[Future, AsyncSeed, Boolean] = nextLong.map(long => long >= 0)


  def createRobot: StateT[Future, AsyncSeed, Robot] = for {
    id <- nextLong
    sentient <- nextBoolean
    isCatherine <- nextBoolean
    isReplicant <- nextBoolean

    name = if (isCatherine) "Catherine" else "Carlos"
    model = if (isReplicant) "replicant" else "borg"
  } yield Robot(id, sentient, name, model)


  // TESTING:
  val initialSeed = AsyncSeed(System.currentTimeMillis())

  /* Use .run() when want to return the final State from the function */
//  val (finalState, robot1) = createRobot.run(initialSeed).value.get
  // finalState: Seed = Seed(2999987205171331217L)
  // robot1: Robot = Robot(13L, false, "Catherine", "replicant")

  /* Use .runA() when don't want to return the final State */
  val robot2 = createRobot.runA(initialSeed).value
  println(robot2)
  // robot2: Robot = Robot(13L, false, "Catherine", "replicant")




  // Issue: These will al return same result as they have same initialSeed
  createRobot.runA(initialSeed).value
  createRobot.runA(initialSeed).value
  createRobot.runA(initialSeed).value


}
