package data_types.StateDemo

import cats.data.State

/* Using State built-in datatype, instead of our User-built Seed
*
*  State is capable of keeping track of the next State, and is immutable (thus is concurrent safe)
*  State is basically a function: S => (S, A), where S is a type representing State
*
*  The flatMap method on State allows it to be used in for-comprehensions
* */

object RandomConstructor2 extends App {

  final case class Robot(id: Long, sentient: Boolean, name: String, model: String)

  final case class Seed(long: Long) {
    def next: Seed = Seed(long * 6364136223846793005L + 1442695040888963407L)   // Returns a new Seed containing a new random value
  }

  // To get the next number or boolean
  val nextLong: State[Seed, Long] = State(seed => (seed.next, seed.long))     // State.apply() requires arg f: S => (S + A), where 1st S is the current State, A is the current State's value & 2nd S is the next State
  val nextBoolean: State[Seed, Boolean] = nextLong.map(long => long >= 0)     // Uses nextLong, but makes a boolean from the long returned


  def createRobot: State[Seed, Robot] = for {
    id <- nextLong
    sentient <- nextBoolean
    isCatherine <- nextBoolean
    isReplicant <- nextBoolean

    name = if (isCatherine) "Catherine" else "Carlos"
    model = if (isReplicant) "replicant" else "borg"
  } yield Robot(id, sentient, name, model)


  // TESTING:
  val initialSeed = Seed(System.currentTimeMillis())

  /* Use .run() when want to return the final State from the function */
  val (finalState, robot1) = createRobot.run(initialSeed).value
  // finalState: Seed = Seed(2999987205171331217L)
  // robot1: Robot = Robot(13L, false, "Catherine", "replicant")

  /* Use .runA() when don't want to return the final State */
  val robot2 = createRobot.runA(initialSeed).value
  // robot2: Robot = Robot(13L, false, "Catherine", "replicant")




  // These will return different robots - passing the final seed from previous calc to next calc
  val (s1, r1) = createRobot.run(initialSeed).value
  val (s2, r2) = createRobot.run(s1).value
  val (s3, r3) = createRobot.run(s2).value

  println(s"$r1, $r2, $r3")


}
