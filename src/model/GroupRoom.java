package model;

import enums.Status;

public class GroupRoom extends Space {
    private int tableNumber;
    private int seatNumber;

    public GroupRoom(int tableNumber, int seatNumber) {
        super("T" + tableNumber + "-" + seatNumber,
              tableNumber + "번 테이블 " + seatNumber + "번 좌석");
        this.tableNumber = tableNumber;
        this.seatNumber = seatNumber;
    }

    @Override
    public String display(int currentSlot) {
        Status status = getCurrentStatus(currentSlot);
        return switch (status) {
            case FREE     -> "[ 빈자리 ]";
            case RESERVED -> "[ 예약됨 ]";
            case IN_USE   -> "[ 사용중 ]";
            default       -> "[ 빈자리 ]";
        };
    }

    public int getTableNumber() { return tableNumber; }
    public int getSeatNumber() { return seatNumber; }
}
