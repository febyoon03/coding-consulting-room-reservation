package service;

import model.User;
import java.util.List;
import java.util.Map;

/**
 * 시간 규칙 엔진 - 의존성 주입(DI) 포인트
 * ReservationService를 생성자로 받아서 시간 경과 비즈니스 로직 처리
 */
public class TimeRuleEngine {
    private final ReservationService reservationService;
    private int currentTime; // 분 단위 (0 = 09:00)

    public TimeRuleEngine(ReservationService reservationService) {
        this.reservationService = reservationService;
        this.currentTime = 0;
    }

    public int getCurrentSlot() {
        return currentTime / 30;
    }

    public String getCurrentTimeString() {
        int hour = 9 + currentTime / 60;
        int min = currentTime % 60;
        return String.format("%02d:%02d", hour, min);
    }

    public List<String> advanceTime(int minutes, Map<String, User> users) {
        this.currentTime += minutes;
        int currentSlot = getCurrentSlot();

        List<String> events = reservationService.simulateTimePass(currentSlot, users);

        if (!events.isEmpty()) {
            System.out.println("\n[시간 경과 이벤트] " + getCurrentTimeString());
            for (String event : events) {
                System.out.println("  -> " + event);
            }
        }
        return events;
    }

    public int getCurrentTime() { return currentTime; }
}
