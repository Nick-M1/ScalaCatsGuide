package Case_Studies.GCounter

/** Commutative Replicated Data Types (CRDTs): <p>
 *  A family of data structures that can be used to reconcile eventually consistent data. <p>
 *
 * GCounter: <br>
 * A CRDT that is a distributed increment‐only counter that can be used, for example, to count the number <br>
 *  of visitors to a web site where requests are served by many web servers. <p>
 *
 * Works by each machine storing a separate counter for every machine it knows about (including itself). <br>
 *  Machines are only allowed to increment its own counter (for each visit to its server / webpage) <br>
 *  When 2 machines 'reconcile' their counters, they take the largest value stored for each machine <p>
 *
 * GCounters allow each machine to keep an accurate account of the state of the whole system without storing the complete history of interactions. <br>
 * If a machine wants to calculate the total traffic for the whole web site, it sums up all the per‐machine counters. <br>
*/

object Demo1 extends App {

  // A GCounter is stored for/on each machine in network
  // store each computer's ID as a String

  final case class GCounter(counters: Map[String, Int]) { // stores Map( machineId -> its counter )

    def increment(machine: String, amount: Int): GCounter = { // amount = clicks on this machine for this round, counters.getOrElse() is the clicks for previous rounds
      val value = amount + counters.getOrElse(machine, 0)
      GCounter(counters + (machine -> value))
    }

    def merge(that: GCounter): GCounter = // merge 2 machines (1 GCounter on each) together
      GCounter(that.counters ++ this.counters.map {
        case (k, v) =>
          k -> v.max(that.counters.getOrElse(k, 0))
      })

    def total: Int = // total clicks on all machines
      counters.values.sum
  }


}
