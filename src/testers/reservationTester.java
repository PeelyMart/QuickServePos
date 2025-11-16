package testers;

import Controller.ReservationController;
import Model.Reservations;

import java.time.LocalDateTime;

public class reservationTester {

    public static void main(String[] args){
        ReservationController.addReservation(1, "Philip", LocalDateTime.now());
    }
}
