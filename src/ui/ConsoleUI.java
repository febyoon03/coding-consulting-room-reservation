package ui;

import model.*;
import service.*;
import java.util.*;

public class ConsoleUI {
    private Scanner scanner;
    private ReservationService resService;
    private TimeRuleEngine timeEngine;
    private Map<String, User> users;

    public ConsoleUI(Scanner scanner, ReservationService resService,
                     TimeRuleEngine timeEngine, Map<String, User> users) {
        this.scanner = scanner;
        this.resService = resService;
        this.timeEngine = timeEngine;
        this.users = users;
    }

    // ========== 공통 ==========

    public void displaySeats() {
        int slot = timeEngine.getCurrentSlot();
        System.out.println();
        System.out.println("****************************************************");
        System.out.println("*   코딩컨설팅룸 좌석 현황                         *");
        System.out.println("*   현재 시간: " + timeEngine.getCurrentTimeString()
                + "  |  " + (resService.isExamPeriod() ? "시험기간" : "평상시")
                + "                       *");
        System.out.println("****************************************************");

        Map<String, Space> spaces = resService.getSpaces();

        for (int t = 1; t <= 4; t++) {
            System.out.print("*  " + t + "번 테이블: ");
            for (int s = 1; s <= 4; s++) {
                String id = "T" + t + "-" + s;
                Space space = spaces.get(id);
                if (space != null) {
                    System.out.print(space.display(slot) + " ");
                }
            }
            System.out.println("*");
        }

        System.out.println("****************************************************");
        System.out.println("*  범례: [ 빈자리 ] [ 예약됨 ] [ 사용중 ]          *");
        System.out.println("****************************************************");
    }

    // ========== 학생 ==========

    public void handleStudentMenu(Student student) {
        boolean running = true;

        while (running) {
            System.out.println(student.getMenu());
            System.out.print("선택 > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"  -> displaySeats();
                case "2"  -> doReservation(student);
                case "3"  -> doCancelReservation(student);
                case "4"  -> doCheckIn(student);
                case "5"  -> doCheckOut(student);
                case "6"  -> showMyReservation(student);
                case "0"  -> { System.out.println("로그아웃 되었습니다."); running = false; }
                default   -> System.out.println("[오류] 잘못된 입력입니다.");
            }
        }
    }

    private void doReservation(Student student) {
        System.out.println("\n=== 예약하기 ===");
        displaySeats();

        System.out.print("테이블 번호 (1~4) > ");
        int table = readInt();
        System.out.print("좌석 번호 (1~4) > ");
        int seat = readInt();
        String spaceId = "T" + table + "-" + seat;

        System.out.println("\n시간 슬롯 안내 (09:00 = 0, 09:30 = 1, 10:00 = 2, ...)");
        System.out.println("  현재 최대 " + resService.getMaxHours() + "시간 ("
                + (resService.isExamPeriod() ? "시험기간" : "평상시") + ")");
        System.out.print("시작 슬롯 > ");
        int startSlot = readInt();
        System.out.print("종료 슬롯 > ");
        int endSlot = readInt();

        System.out.print("동반자 학번 (쉼표 구분, 1명 이상) > ");
        String companionInput = scanner.nextLine().trim();
        List<String> companions = Arrays.asList(companionInput.split("\\s*,\\s*"));

        String result = resService.makeReservation(student, spaceId, startSlot, endSlot, companions);
        System.out.println("\n" + result);
    }

    private void doCancelReservation(Student student) {
        System.out.println("\n=== 예약 취소 ===");
        if (!student.hasActiveReservation()) {
            System.out.println("[실패] 활성 예약이 없습니다.");
            return;
        }
        Reservation r = student.getActiveReservation();
        System.out.println("현재 예약: " + r.getId() + " | "
                + resService.getSpaces().get(r.getSpaceId()).getName() + " | " + r.getTimeString());
        System.out.print("정말 취소하시겠습니까? (Y/N) > ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println(resService.cancelReservation(student));
        } else {
            System.out.println("취소를 중단했습니다.");
        }
    }

    private void doCheckIn(Student student) {
        System.out.println("\n=== 입장 처리 ===");
        System.out.println(resService.checkIn(student, timeEngine.getCurrentSlot()));
    }

    private void doCheckOut(Student student) {
        System.out.println("\n=== 퇴장 처리 ===");
        System.out.println(resService.checkOut(student));
    }

    private void showMyReservation(Student student) {
        System.out.println("\n=== 내 예약 정보 ===");
        if (!student.hasActiveReservation()) {
            System.out.println("활성 예약이 없습니다.");
        } else {
            Reservation r = student.getActiveReservation();
            Space space = resService.getSpaces().get(r.getSpaceId());
            System.out.println("예약번호 : " + r.getId());
            System.out.println("좌석     : " + (space != null ? space.getName() : r.getSpaceId()));
            System.out.println("시간     : " + r.getTimeString());
            System.out.println("상태     : " + r.getStatus().getLabel());
            System.out.println("동반자   : " + String.join(", ", r.getCompanions()));
        }
        System.out.println("노쇼 횟수 : " + student.getNoShowCount() + "/3");
        System.out.println("이용 제한 : " + (student.isRestricted() ? student.getRestrictReason() : "없음"));
    }

    // ========== 관리자 ==========

    public void handleAdminMenu(Admin admin) {
        boolean running = true;

        while (running) {
            System.out.println(admin.getMenu());
            System.out.print("선택 > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displaySeats();
                case "2" -> toggleExamPeriod();
                case "3" -> doForceReturn();
                case "4" -> doManageRestrictions();
                case "5" -> doTimeSimulation();
                case "0" -> { System.out.println("로그아웃 되었습니다."); running = false; }
                default  -> System.out.println("[오류] 잘못된 입력입니다.");
            }
        }
    }

    private void toggleExamPeriod() {
        resService.setExamPeriod(!resService.isExamPeriod());
        String mode = resService.isExamPeriod() ? "시험기간" : "평상시";
        System.out.println("[성공] " + mode + " 모드로 전환! (최대 " + resService.getMaxHours() + "시간)");
    }

    private void doForceReturn() {
        System.out.println("\n=== 강제 반납 ===");
        displaySeats();
        System.out.print("강제 반납할 좌석 ID (예: T1-1) > ");
        String spaceId = scanner.nextLine().trim();
        System.out.println(resService.forceReturn(spaceId, timeEngine.getCurrentSlot()));
    }

    private void doManageRestrictions() {
        System.out.println("\n=== 이용 제한 관리 ===");
        System.out.println("1. 제한 학생 목록 조회");
        System.out.println("2. 제한 해제");
        System.out.print("선택 > ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            boolean found = false;
            for (User u : users.values()) {
                if (u instanceof Student s && s.isRestricted()) {
                    System.out.println("  " + s.getId() + " (" + s.getName()
                            + ") - 사유: " + s.getRestrictReason() + " | 노쇼: " + s.getNoShowCount() + "회");
                    found = true;
                }
            }
            if (!found) System.out.println("  제한된 학생이 없습니다.");
        } else if (choice.equals("2")) {
            System.out.print("해제할 학번 > ");
            String sid = scanner.nextLine().trim();
            User u = users.get(sid);
            if (u instanceof Student s && s.isRestricted()) {
                s.setRestricted(false);
                s.setRestrictReason(null);
                System.out.println("[성공] " + sid + " 이용 제한 해제 완료");
            } else {
                System.out.println("[실패] 해당 학생을 찾을 수 없거나 제한 상태가 아닙니다.");
            }
        }
    }

    private void doTimeSimulation() {
        System.out.println("\n=== 시간 경과 시뮬레이션 ===");
        System.out.println("현재 시간: " + timeEngine.getCurrentTimeString());
        System.out.print("경과할 시간 (분) > ");
        int minutes = readInt();
        if (minutes <= 0) {
            System.out.println("[오류] 1분 이상 입력하세요.");
            return;
        }
        timeEngine.advanceTime(minutes, users);
        System.out.println("[완료] 현재 시간: " + timeEngine.getCurrentTimeString());
    }

    // ========== 유틸 ==========

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[오류] 숫자를 입력하세요.");
            return -1;
        }
    }
}
