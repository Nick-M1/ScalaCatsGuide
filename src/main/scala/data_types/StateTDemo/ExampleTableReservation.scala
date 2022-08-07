package data_types.StateTDemo

import cats.data.{StateT, NonEmptyList}
import cats.syntax.all._
import java.time.LocalTime

/* TABLE RESERVATION SYSTEM:
*  Keep track of the tables and the current reservations for each of them.
*  The end-user can then try to insert a booking for a specific table and time.
*  If such a table is available, then the booking is placed and the state is updated, otherwise, an error is returned.
*
*  To simplify the logic, for each reservation we will just consider a single LocalTime starting at the beginning of the hour.
*
*  We need to implement/define:
*  - The type representing the reservation.
*  - The type representing the state of the Table Reservation System. It will wrap around a collection of Reservations.
*  - An initial state, that will be just empty (no reservations).
*  - A custom Throwable to be used in case of an error.
*  - The logic for the booking insertion. We can take advantage of the method modifyF later on to apply it to the system state.
*
* */


object ExampleTableReservation {

  // Use Either[Throwable, A] to model: success as Right(a) or failure Left(ex)
  type ThrowableOr[A] = Either[Throwable, A]


  final case class ReservationId(tableNumber: Int, hour: LocalTime)
  final case class Reservation(id: ReservationId, name: String)

  // Stores all reservations & allows adding new reservations
  final case class Reservations(reservations: List[Reservation]) {
    def insert(reservation: Reservation): ThrowableOr[Reservations] =
      if (reservations.exists(r => r.id == reservation.id))             // If a reservation for this same tableNumber & time already exists -> throw error
        Left(new TableAlreadyReservedException(reservation))
      else Right(Reservations(reservations :+ reservation))             // Else, add this reservation to the reservations (success)
  }

  // Custom exception - Reservation already exists
  final class TableAlreadyReservedException(reservation: Reservation) extends RuntimeException(
    s"${reservation.name} cannot be added because table number ${reservation.id.tableNumber} is already reserved for the ${reservation.id.hour}"
  )

  // Base State - Reservations List is empty
  val emptyReservationSystem: Reservations = Reservations(List.empty)

  // Insert a new Reservation - modify State
  def insertBooking(reservation: Reservation): StateT[ThrowableOr, Reservations, Unit] =
    StateT.modifyF[ThrowableOr, Reservations](_.insert(reservation))

  // Given list of Reservations to be made, add them to our Reservations list
  def processBookings(bookings: NonEmptyList[Reservation]): ThrowableOr[Reservations] =
    bookings
      .traverse_(insertBooking)
      .runS(emptyReservationSystem)




  // TESTING: ---------------------
  val bookings: NonEmptyList[Reservation] = NonEmptyList.of(
    Reservation(
      ReservationId(tableNumber = 1, hour = LocalTime.parse("10:00:00")),
      name = "Gandalf"
    ),
    Reservation(
      ReservationId(tableNumber = 2, hour = LocalTime.parse("10:00:00")),
      name = "Legolas"
    ),
    Reservation(
      ReservationId(tableNumber = 1, hour = LocalTime.parse("12:00:00")),
      name = "Frodo"
    ),
    Reservation(
      ReservationId(tableNumber = 2, hour = LocalTime.parse("12:00:00")),
      name = "Bilbo"
    ),
    Reservation(
      ReservationId(tableNumber = 3, hour = LocalTime.parse("13:00:00")),
      name = "Elrond"
    ),
    Reservation(
      ReservationId(tableNumber = 1, hour = LocalTime.parse("16:00:00")),
      name = "Sauron"
    ),
    Reservation(
      ReservationId(tableNumber = 2, hour = LocalTime.parse("16:00:00")),
      name = "Aragorn"
    ),
    Reservation(
      ReservationId(tableNumber = 2, hour = LocalTime.parse("18:00:00")),
      name = "Gollum"
    )
  )
  // bookings: NonEmptyList[Reservation] = NonEmptyList(
  //   Reservation(ReservationId(1, 10:00), "Gandalf"),
  //   List(
  //     Reservation(ReservationId(2, 10:00), "Legolas"),
  //     Reservation(ReservationId(1, 12:00), "Frodo"),
  //     Reservation(ReservationId(2, 12:00), "Bilbo"),
  //     Reservation(ReservationId(3, 13:00), "Elrond"),
  //     Reservation(ReservationId(1, 16:00), "Sauron"),
  //     Reservation(ReservationId(2, 16:00), "Aragorn"),
  //     Reservation(ReservationId(2, 18:00), "Gollum")
  //   )
  // )


  processBookings(bookings)
  // res1: ThrowableOr[Reservations] = Right(
  //   Reservations(
  //     List(
  //       Reservation(ReservationId(1, 10:00), "Gandalf"),
  //       Reservation(ReservationId(2, 10:00), "Legolas"),
  //       Reservation(ReservationId(1, 12:00), "Frodo"),
  //       Reservation(ReservationId(2, 12:00), "Bilbo"),
  //       Reservation(ReservationId(3, 13:00), "Elrond"),
  //       Reservation(ReservationId(1, 16:00), "Sauron"),
  //       Reservation(ReservationId(2, 16:00), "Aragorn"),
  //       Reservation(ReservationId(2, 18:00), "Gollum")
  //     )
  //   )
  // )

  processBookings(
    bookings :+ Reservation(
      ReservationId(tableNumber = 1, hour = LocalTime.parse("16:00:00")),
      name = "Saruman"
    )
  )
  // res2: ThrowableOr[Reservations] = Left(
  //   repl.MdocSession$App0$TableAlreadyReservedException: Saruman cannot be added because table number 1 is already reserved for the 16:00
  // )


}
