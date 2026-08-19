package model;

import java.util.List;

public interface Reservable {
    boolean reserve(Reservation reservation);
    boolean cancel(String reservationId);
    boolean isAvailable(int startSlot, int endSlot);
    List<Reservation> getReservations();
}
