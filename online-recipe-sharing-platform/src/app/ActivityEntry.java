package app;

public class ActivityEntry {

    public enum Type {
        RECIPE_CREATED,
        RECIPE_UPDATED,
        RECIPE_DELETED,
        REVIEW_ADDED,
        FAVOURITE_TOGGLED
    }

    private final String username;
    private final Type type;
    private final String description;
    private final String timestamp;

    public ActivityEntry(String username, Type type, String description, String timestamp) {
        this.username = username;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getUsername() { return username; }
    public Type getType() { return type; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
}