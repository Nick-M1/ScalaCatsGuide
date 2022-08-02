package datatypes.WriterTDemo

import cats.data.{WriterT, Writer}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import cats.instances.option._


/* WriterT[F[_], L, V] is a type wrapper on an F[(L, V)]. It is a monad transformer for Writer
*  WriterT can be more convenient to work with than using F[Writer[L, V]] directly,
*   because it exposes operations that allow you to work with the values of the inner Writer (L and V)
*   abstracting both the F and Writer.*/

object Demo {
  WriterT[Option, String, Int](Some(("value", 10))).map(x => x * x)   // res: WriterT[Option, String, Int] = WriterT(Some(("value", 100)))

  // USING OPTION & EITHER:
  /* If one of the computations has a None or a Left, the whole computation will return a None or a Left
     If the computation succeeds, the logging side of the Writers will be combined.                           */

  val optionWriterT1 : WriterT[Option, String, Int] = WriterT(Some(("writerT value 1 ", 123)))
  val optionWriterT2 : WriterT[Option, String, Int] = WriterT(Some(("writerT value 1 ", 123)))
  val optionWriterT3 : WriterT[Option, String, Int] = WriterT.valueT(None)

  val eitherWriterT1 : WriterT[Either[String, *], String, Int] = WriterT(Right(("writerT value 1 ", 123)))
  val eitherWriterT2 : WriterT[Either[String, *], String, Int] = WriterT(Right(("writerT value 1 ", 123)))
  val eitherWriterT3 : WriterT[Either[String, *], String, Int] = WriterT.valueT(Left("error!!!"))


  // This returns a Some, since both are Some
  for {
    v1 <- optionWriterT1
    v2 <- optionWriterT2
  } yield v1 + v2
  // res: WriterT[Option, String, Int] = WriterT(Some(("writerT value 1 writerT value 1 ", 246)))

  // This returns a None, since one is a None
  for {
    v1 <- optionWriterT1
    v2 <- optionWriterT2
    v3 <- optionWriterT3
  } yield v1 + v2 + v3
  // res: WriterT[Option, String, Int] = WriterT(None)


  // This returns a Right, since both are Right
  for {
    v1 <- eitherWriterT1
    v2 <- eitherWriterT2
  } yield v1 + v2
  // res3: WriterT[Either[String, β$0$], String, Int] = WriterT(Right(("writerT value 1writerT value 1", 246)))

  // This returns a Left since one is a Left
  for {
    v1 <- eitherWriterT1
    v2 <- eitherWriterT2
    v3 <- eitherWriterT3
  } yield v1 + v2 + v3
  // res4: WriterT[Either[String, β$0$], String, Int] = WriterT(Left("error!!!"))


  // USING VALIDATED:
  /* Doesnt short-circuit like either/option, instead accumulates errors */
  val validatedWriterT1 : WriterT[Validated[String, *], String, Int] = WriterT(Valid(("writerT value 1", 123)))
  val validatedWriterT2 : WriterT[Validated[String, *], String, Int] = WriterT(Valid(("writerT value 1", 123)))
  val validatedWriterT3 : WriterT[Validated[String, *], String, Int] = WriterT(Invalid("error 1!!!") : Validated[String, (String, Int)])
  val validatedWriterT4 : WriterT[Validated[String, *], String, Int] = WriterT(Invalid("error 2!!!"): Validated[String, (String, Int)])

  // This returns a Right since both are Right
  (validatedWriterT1, validatedWriterT2)
    .mapN((v1, v2) => v1 + v2)
  // res: WriterT[Validated[String, β$4$], String, Int] = WriterT(
  //   Valid(("writerT value 1writerT value 1", 246))
  // )

  // This returns a Left since there are several Left
  (validatedWriterT1, validatedWriterT2, validatedWriterT3, validatedWriterT4 )
    .mapN((v1, v2, v3, v4) => v1 + v2 + v3 + v4)
  // res: WriterT[Validated[String, β$6$], String, Int] = WriterT(
  //   Invalid("error 1!!!error 2!!!")
  // )




  // CONSTRUCTING A WRITER-T:

  // WriterT[F[_], L, V](run: F[(L, V)]):
  /* This is the constructor of the datatype itself. It just builds the type starting from the full wrapped value. */
  val val1: Option[(String, Int)] = Some(("value", 123))        // Option is the F[_]
  WriterT(val1)            // res: WriterT[Option, String, Int] = WriterT(Some(("value", 123)))

  // liftF[F[_], L, V](fv: F[V])(implicit monoidL: Monoid[L], F: Applicative[F]): WriterT[F, L, V]:
  /* Builds WriterT from value V wrapped into an F. It requires:
     - Monoid[L], since it uses the empty value from the type class to fill the L value not specified in the input.
     - Applicative[F] to modify the inner value.                                                                        */
  val val2: Option[Int] = Some(123)
  WriterT.liftF[Option, String, Int](val2)        // res: WriterT[Option, String, Int] = WriterT(Some(("", 123)))

  // put[F[_], L, V](v: V)(l: L)(implicit applicativeF: Applicative[F]): WriterT[F, L, V]:
  /* If there is an Applicative instance of F, this function creates the datatype starting from the inner Writer's values. */
  WriterT.put[Option, String, Int](123)("initial value")      // res: WriterT[Option, String, Int] = WriterT(Some(("initial value", 123)))

  // putT[F[_], L, V](vf: F[V])(l: L)(implicit functorF: Functor[F]): WriterT[F, L, V]:
  /* Same as put, but the value V is already wrapped into F */
  WriterT.putT[Option, String, Int](Some(123))("initial value")     // res: WriterT[Option, String, Int] = WriterT(Some(("initial value", 123)))




}
