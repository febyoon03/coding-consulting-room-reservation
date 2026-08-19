package model;

import enums.Status;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private static int counter = 0;

    private String id;
    private String studentId;
    private String spaceId;
    private int startSlot;          // 30분 단위 (0 = 09:00, 1 = 09:30 ...)
    private int endSlot;
    private Status status;
    private List<String> companions;
    private boolean checkedIn;

    public Reservation(String studentId, String spaceId, int startSlot, int endSlot, List<String> companions) {
        this.id = "R" + String.format("%04d", ++counter);
        this.studentId = studentId;
        this.spaceId = spaceId;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.status = Status.RESERVED;
        this.companions = companions != null ? companions : new ArrayList<>();
        this.checkedIn = false;
    }

    public boolean conflictsWith(int otherStart, int otherEnd) {
        return this.startSlot < otherEnd && otherStart < this.endSlot;
    }

    public String getTimeString() {
        return slotToTime(startSlot) + " ~ " + slotToTime(endSlot);
    }

    public static String slotToTime(int slot) {
        int hour = 9 + (slot * 30) / 60;
        int min = (slot * 30) % 60;
        return String.format("%02d:%02d", hour, min);
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getSpaceId() { return spaceId; }
    public int getStartSlot() { return startSlot; }
    public int getEndSlot() { return endSlot; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<String> getCompanions() { return companions; }
    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
}
