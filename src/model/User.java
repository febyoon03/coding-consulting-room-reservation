package model;

public abstract class User {
    protected String id;
    protected String password;
    protected String name;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public boolean login(String inputId, String inputPw) {
        return this.id.equals(inputId) && this.password.equals(inputPw);
    }

    // 다형성 포인트: Student/Admin이 각각 다른 메뉴 반환
    public abstract String getMenu();
    public abstract String getRole();

    public String getId() { return id; }
    public String getName() { return name; }
}
