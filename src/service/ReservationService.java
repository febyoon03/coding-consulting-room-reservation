package service;

import enums.Status;
import model.*;
import java.util.*;

public class ReservationService {
    private Map<String, Space> spaces;
    private boolean examPeriod;

    public ReservationService(Map<String, Space> spaces) {
        this.spaces = spaces;
        this.examPeriod = false;
    }

    public int getMaxHours() {
        return examPeriod ? 3 : 4;
    }

    public int getMaxSlots() {
        return getMaxHours() * 2;
    }

    public String makeReservation(Student student, String spaceId, int startSlot, int endSlot, List<String> companions) {
        if (student.isRestricted()) {
            return "[실패] 이용이 제한된 상태입니다. 사유: " + student.getRestrictReason();
        }
        if (student.hasActiveReservation()) {
            return "[실패] 이미 활성 예약이 있습니다. (예약번호: " + student.getActiveReservation().getId() + ")";
        }
        if (endSlot - startSlot > getMaxSlots()) {
            return "[실패] 최대 " + getMaxHours() + "시간까지 예약 가능합니다.";
        }
        if (endSlot - startSlot < 1) {
            return "[실패] 최소 30분 이상 예약해야 합니다.";
        }
        if (companions == null || companions.size() < 1) {
            return "[실패] 본인 포함 2명 이상의 동반 학번이 필요합니다.";
        }
        Space space = spaces.get(spaceId);
        if (space == null) {
            return "[실패] 존재하지 않는 좌석입니다.";
        }
        if (!space.isAvailable(startSlot, endSlot)) {
            return "[실패] 해당 시간에 이미 예약이 있습니다.";
        }

        Reservation reservation = new Reservation(student.getId(), spaceId, startSlot, endSlot, companions);
        space.reserve(reservation);
        student.setActiveReservation(reservation);

        return "[성공] 예약 완료! 예약번호: " + reservation.getId()
                + " | " + space.getName() + " | " + reservation.getTimeString();
    }

    public String cancelReservation(Student student) {
        if (!student.hasActiveReservation()) {
            return "[실패] 활성 예약이 없습니다.";
        }
        Reservation r = student.getActiveReservation();
        if (r.getStatus() == Status.IN_USE) {
            return "[실패] 이미 입장한 예약은 취소할 수 없습니다. 퇴장 처리를 이용하세요.";
        }

        Space space = spaces.get(r.getSpaceId());
        space.cancel(r.getId());
        student.setActiveReservation(null);

        return "[성공] 예약 취소 완료 (" + r.getId() + ")";
    }

    public String checkIn(Student student, int currentSlot) {
        if (!student.hasActiveReservation()) {
            return "[실패] 활성 예약이 없습니다.";
        }
        Reservation r = student.getActiveReservation();
        if (r.getStatus() != Status.RESERVED) {
            return "[실패] 입장 가능한 상태가 아닙니다. (현재: " + r.getStatus().getLabel() + ")";
        }
        if (currentSlot < r.getStartSlot()) {
            return "[실패] 아직 예약 시간이 아닙니다. (" + r.getTimeString() + ")";
        }

        r.setStatus(Status.IN_USE);
        r.setCheckedIn(true);

        return "[성공] 입장 완료! " + spaces.get(r.getSpaceId()).getName() + " | " + r.getTimeString();
    }

    public String checkOut(Student student) {
        if (!student.hasActiveReservation()) {
            return "[실패] 활성 예약이 없습니다.";
        }
        Reservation r = student.getActiveReservation();
        if (r.getStatus() != Status.IN_USE) {
            return "[실패] 입장 상태가 아닙니다.";
        }

        r.setStatus(Status.DONE);
        student.setActiveReservation(null);

        return "[성공] 퇴장 완료!";
    }

    public String forceReturn(String spaceId, int currentSlot) {
        Space space = spaces.get(spaceId);
        if (space == null) return "[실패] 존재하지 않는 좌석입니다.";

        Reservation r = space.getActiveReservation(currentSlot);
        if (r == null) return "[실패] 해당 좌석에 활성 예약이 없습니다.";

        r.setStatus(Status.DONE);
        return "[성공] 강제 반납 완료: " + space.getName() + " (학번: " + r.getStudentId() + ")";
    }

    public List<String> simulateTimePass(int currentSlot, Map<String, User> users) {
        List<String> events = new ArrayList<>();

        for (Space space : spaces.values()) {
            for (Reservation r : space.getReservations()) {
                // 예약 후 미입장 → 노쇼 (시작 슬롯 + 1 이상 경과)
                if (r.getStatus() == Status.RESERVED && currentSlot >= r.getStartSlot() + 1) {
                    r.setStatus(Status.CANCELED);
                    User user = users.get(r.getStudentId());
                    if (user instanceof Student student) {
                        student.addNoShow();
                        student.setActiveReservation(null);
                        events.add("노쇼: " + r.getStudentId() + " (" + space.getName() + ") - 노쇼 " + student.getNoShowCount() + "회");
                        if (student.isRestricted()) {
                            events.add(">> " + r.getStudentId() + " 이용 제한 발동 (노쇼 3회)");
                        }
                    }
                }

                // 이용 시간 종료 → 자동 퇴장
                if (r.getStatus() == Status.IN_USE && currentSlot >= r.getEndSlot()) {
                    r.setStatus(Status.DONE);
                    User user = users.get(r.getStudentId());
                    if (user instanceof Student student) {
                        student.setActiveReservation(null);
                    }
                    events.add("이용종료: " + r.getStudentId() + " (" + space.getName() + ")");
                }
            }
        }
        return events;
    }

    public Map<String, Space> getSpaces() { return spaces; }
    public boolean isExamPeriod() { return examPeriod; }
    public void setExamPeriod(boolean examPeriod) { this.examPeriod = examPeriod; }
}
