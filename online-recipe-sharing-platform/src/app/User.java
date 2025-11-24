package app;

public class User {
    public enum Role {
        ADMIN,
        CONTRIBUTOR,
        EXPLORER
    }

    private final String username;
    private final String password; // demo only, plain text
    private final Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isContributor() {
        return role == Role.CONTRIBUTOR;
    }

    public boolean isExplorer() {
        return role == Role.EXPLORER;
    }
}
