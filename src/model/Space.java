package model;

import enums.Status;
import java.util.ArrayList;
import java.util.List;

public abstract class Space implements Reservable {
    protected String id;
    protected String name;
    protected List<Reservation> reservations;

    public Space(String id, String name) {
        this.id = id;
        this.name = name;
        this.reservations = new ArrayList<>();
    }

    // 다형성 포인트: GroupRoom이 오버라이딩
    public abstract String display(int currentSlot);

    @Override
    public boolean reserve(Reservation reservation) {
        if (!isAvailable(reservation.getStartSlot(), reservation.getEndSlot())) {
            return false;
        }
        reservations.add(reservation);
        return true;
    }

    @Override
    public boolean cancel(String reservationId) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationId) && r.getStatus() == Status.RESERVED) {
                r.setStatus(Status.CANCELED);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAvailable(int startSlot, int endSlot) {
        for (Reservation r : reservations) {
            if (r.getStatus() == Status.RESERVED || r.getStatus() == Status.IN_USE) {
                if (r.conflictsWith(startSlot, endSlot)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Reservation> getReservations() {
        return reservations;
    }

    public Reservation getActiveReservation(int currentSlot) {
        for (Reservation r : reservations) {
            if ((r.getStatus() == Status.RESERVED || r.getStatus() == Status.IN_USE)
                    && r.getStartSlot() <= currentSlot && currentSlot < r.getEndSlot()) {
                return r;
            }
        }
        return null;
    }

    public Status getCurrentStatus(int currentSlot) {
        Reservation r = getActiveReservation(currentSlot);
        if (r == null) return Status.FREE;
        return r.getStatus();
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
