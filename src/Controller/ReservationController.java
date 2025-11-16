package Controller;


import Model.Reservations;
import DAO.ReservationDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ReservationController {

    public static Reservations addReservation(int tableID, String name, LocalDateTime time){
        Reservations rs = new Reservations(-1, tableID, name, time, true);
        boolean result = ReservationDAO.addReservation(rs);
        if(result && rs.getRequestId() != -1){
            System.out.println("[DEBUG] Reservation complete request id: " + rs.getRequestId());
            return rs;
        }
        System.out.println("[DEBUG] Reservation failed" + rs.getRequestId()); //
        return null;
    }


    /**
     * This will check all active reservations and check if they are still valid.
     * If the rsvp time already passed, mark it inactive
     */
    public static void updateValidity(List<ArrayList> activeReservations){

    }




}
