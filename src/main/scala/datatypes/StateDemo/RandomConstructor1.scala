package datatypes.StateDemo

object RandomConstructor1 extends App {

  final case class Robot(id: Long, sentient: Boolean, name: String, model: String)


  /* Creating an instance of Robot with random values
     However, scala.util.Random() is mutable & changes each time it is called (to get to next number in its list)   */

  val rng = new scala.util.Random(0L)

  def createRobot_v1(): Robot = {
    val id = rng.nextLong()
    val sentient = rng.nextBoolean()
    val name = if (rng.nextBoolean()) "Catherine" else "Carlos"
    val model = if (rng.nextBoolean()) "replicant" else "borg"
    Robot(id, sentient, name, model)
  }


  createRobot_v1()     // Robot(-4962768465676381896L, false, "Catherine", "replicant")



  // Making an immutable (thus concurrency-safe) version:

  // Start value (to initialise our random Seed)
  final case class Seed(long: Long) {
    def next: Seed = Seed(long * 6364136223846793005L + 1442695040888963407L)   // Returns a new Seed containing a new random value
  }

  // To get the next number or boolean
  def nextBoolean(seed: Seed): (Seed, Boolean) =
    (seed.next, seed.long >= 0L)

  def nextLong(seed: Seed): (Seed, Long) =
    (seed.next, seed.long)              // Each time, returns a Seed (containing a new random value) & the current random value


  def createRobot_v2(seed: Seed): Robot = {
    val (seed1, id) = nextLong(seed)
    val (seed2, sentient) = nextBoolean(seed1)
    val (seed3, isCatherine) = nextBoolean(seed2)
    val (seed4, isReplicant) = nextBoolean(seed3)

    val name = if (isCatherine) "Catherine" else "Carlos"
    val model = if (isReplicant) "replicant" else "borg"
    Robot(id, sentient, name, model)
  }

//  val initialSeed = Seed(13L)
  val initialSeed = Seed(System.currentTimeMillis())
  createRobot_v2(initialSeed)        // robot: Robot = Robot(13L, false, "Catherine", "replicant")

  /* However, if you called createRobot_v2() again with the same Seed, you would get same results (not as random) */

}
