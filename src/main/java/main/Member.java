package main;

public class Member {
    private String id;
    private String name;
    private boolean isAdmin;

    public Member(String id, String name, boolean isAdmin){
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
