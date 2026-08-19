package enums;

public enum Status {
    FREE("빈자리"),
    RESERVED("예약됨"),
    IN_USE("사용중"),
    DONE("이용완료"),
    CANCELED("취소됨");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
