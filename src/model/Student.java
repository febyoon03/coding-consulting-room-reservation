package model;

public class Student extends User {
    private int noShowCount;
    private boolean restricted;
    private String restrictReason;
    private Reservation activeReservation;

    public Student(String id, String password, String name) {
        super(id, password, name);
        this.noShowCount = 0;
        this.restricted = false;
        this.activeReservation = null;
    }

    @Override
    public String getMenu() {
        return """
            ************************************
            *       학생 메뉴                  *
            ************************************
            *  1. 좌석 현황 보기               *
            *  2. 예약하기                     *
            *  3. 예약 취소                    *
            *  4. 입장 처리                    *
            *  5. 퇴장 처리                    *
            *  6. 내 예약 정보                 *
            *  0. 로그아웃                     *
            ************************************""";
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    // 캡슐화 포인트: 내부에서 3회 체크 + 자동 제한
    public void addNoShow() {
        this.noShowCount++;
        if (this.noShowCount >= 3) {
            this.restricted = true;
            this.restrictReason = "노쇼 3회 누적";
        }
    }

    public boolean isRestricted() { return restricted; }
    public void setRestricted(boolean restricted) { this.restricted = restricted; }
    public String getRestrictReason() { return restrictReason; }
    public void setRestrictReason(String reason) { this.restrictReason = reason; }
    public int getNoShowCount() { return noShowCount; }
    public Reservation getActiveReservation() { return activeReservation; }
    public void setActiveReservation(Reservation r) { this.activeReservation = r; }
    public boolean hasActiveReservation() { return activeReservation != null; }
}
