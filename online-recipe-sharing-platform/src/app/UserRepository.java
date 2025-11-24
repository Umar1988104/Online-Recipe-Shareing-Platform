package app;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        // Demo users; in a real app, load from DB or config
        users.add(new User("admin", "admin123", User.Role.ADMIN));
        users.add(new User("contrib", "contrib123", User.Role.CONTRIBUTOR));
        users.add(new User("explorer", "explore123", User.Role.EXPLORER));
    }

    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public synchronized boolean usernameExists(String username) {
        if (username == null || username.isEmpty()) return false;
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addUser(User user) {
        if (user == null) return;
        if (!usernameExists(user.getUsername())) {
            users.add(user);
        }
    }
}
