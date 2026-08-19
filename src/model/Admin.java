package model;

public class Admin extends User {

    public Admin(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public String getMenu() {
        return """
            ************************************
            *       관리자 메뉴                *
            ************************************
            *  1. 좌석 현황 보기               *
            *  2. 시험기간 토글                *
            *  3. 강제 반납                    *
            *  4. 이용 제한 관리               *
            *  5. 시간 경과 시뮬레이션         *
            *  0. 로그아웃                     *
            ************************************""";
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
