package data_types.ChainDemo

import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}


object ChainDemo extends App {

  // CHAIN:
  val a: Chain[Int] = Chain(1, 2, 3)
  val b: Chain[Int] = Chain(4, 5, 6)
  val c: Chain[Int] = a ++ b      // Same as Chain.concat(a, b)   -> Chain(1, 2, 3, 4, 5, 6)

  Chain.fromSeq(Seq("a", "b", "c"))     // Chain(a, b, c)




  /* NON-EMPTY-CHAIN:
     It is the non empty version of Chain.
     It doesn't have a Monoid instance since it cannot be empty, but it does have a Semigroup instance.

     Likewise, it defines a NonEmptyTraverse instance, but no TraverseFilter instance.           */


  // Creating a NonEmptyList:
  NonEmptyChain(1, 2, 3, 4)           // res: NonEmptyChain[Int] = Append(Singleton(1), Wrap(Vector(2, 3, 4)))
  NonEmptyChain.one(1)                // res: NonEmptyChain[Int] = Singleton(1)

  NonEmptyChain.fromNonEmptyList(NonEmptyList(1, List(2, 3)))           // res: NonEmptyChain[Int] = Wrap(List(1, 2, 3))
  NonEmptyChain.fromNonEmptyVector(NonEmptyVector(1, Vector(2, 3)))     // res: NonEmptyChain[Int] = Wrap(Vector(1, 2, 3))


  // Create an Option[NonEmptyChain] - Option wrapper when creating NonEmptyChain from a container that can be empty for safety
  NonEmptyChain.fromChain(Chain(1, 2, 3))     // res: Option[NonEmptyChain[Int]] = Some(Wrap(Vector(1, 2, 3)))
  NonEmptyChain.fromSeq(List.empty[Int])      // res: Option[NonEmptyChain[Int]] = None
  NonEmptyChain.fromSeq(Vector(1, 2, 3))      // res: Option[NonEmptyChain[Int]] = Some(Wrap(Vector(1, 2, 3)))


  // Append or prepend a single element
  NonEmptyChain.fromChainAppend(Chain(1, 2, 3), 4)        // res: NonEmptyChain[Int] = Append(Wrap(Vector(1, 2, 3)), Singleton(4))
  NonEmptyChain.fromChainAppend(Chain.empty[Int], 1)      // res: NonEmptyChain[Int] = Singleton(1)
  NonEmptyChain.fromChainPrepend(1, Chain(2, 3))          // res: NonEmptyChain[Int] = Append(Singleton(1), Wrap(Vector(2, 3)))



}
