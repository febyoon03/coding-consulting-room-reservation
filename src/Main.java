import model.*;
import service.*;
import ui.ConsoleUI;
import java.util.*;

public class Main {
    private static Map<String, User> users = new LinkedHashMap<>();
    private static Map<String, Space> spaces = new LinkedHashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeData();

        ReservationService resService = new ReservationService(spaces);
        TimeRuleEngine timeEngine = new TimeRuleEngine(resService);
        ConsoleUI ui = new ConsoleUI(scanner, resService, timeEngine, users);

        System.out.println("****************************************************");
        System.out.println("*                                                  *");
        System.out.println("*        코딩컨설팅룸 예약시스템                   *");
        System.out.println("*        Coding Consulting Room Reservation        *");
        System.out.println("*                                                  *");
        System.out.println("****************************************************");

        boolean running = true;
        while (running) {
            System.out.println("\n-- 메인 메뉴 --");
            System.out.println("  1. 로그인");
            System.out.println("  2. 회원가입");
            System.out.println("  0. 종료");
            System.out.print("선택 > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> login(ui);
                case "2" -> register();
                case "0" -> {
                    System.out.println("프로그램을 종료합니다.");
                    running = false;
                }
                default -> System.out.println("[오류] 잘못된 입력입니다.");
            }
        }
        scanner.close();
    }

    private static void login(ConsoleUI ui) {
        System.out.println("\n-- 로그인 --");
        System.out.print("학번(ID) > ");
        String id = scanner.nextLine().trim();
        System.out.print("비밀번호 > ");
        String pw = scanner.nextLine().trim();

        User user = users.get(id);
        if (user != null && user.login(id, pw)) {
            System.out.println("\n[성공] 로그인 성공! 환영합니다, " + user.getName() + "님 (" + user.getRole() + ")");

            // 다형성: User 타입으로 받아서 instanceof로 분기
            if (user instanceof Student student) {
                ui.handleStudentMenu(student);
            } else if (user instanceof Admin admin) {
                ui.handleAdminMenu(admin);
            }
        } else {
            System.out.println("[실패] 로그인 실패! 학번 또는 비밀번호를 확인하세요.");
        }
    }

    private static void register() {
        System.out.println("\n-- 회원가입 --");
        System.out.print("학번 > ");
        String id = scanner.nextLine().trim();

        if (users.containsKey(id)) {
            System.out.println("[실패] 이미 등록된 학번입니다.");
            return;
        }

        System.out.print("이름 > ");
        String name = scanner.nextLine().trim();
        System.out.print("비밀번호 > ");
        String pw = scanner.nextLine().trim();

        users.put(id, new Student(id, pw, name));
        System.out.println("[성공] 회원가입 완료! " + id + " (" + name + ")");
    }

    private static void initializeData() {
        // 데모 계정
        users.put("admin", new Admin("admin", "1234", "관리자"));
        users.put("2023100003", new Student("2023100003", "1234", "박민준"));
        users.put("2023100001", new Student("2023100001", "1234", "김철수"));
        users.put("2023100002", new Student("2023100002", "1234", "이영희"));

        // 4테이블 x 4좌석 = 16석
        for (int t = 1; t <= 4; t++) {
            for (int s = 1; s <= 4; s++) {
                String id = "T" + t + "-" + s;
                spaces.put(id, new GroupRoom(t, s));
            }
        }
    }
}
