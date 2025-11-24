package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private final String authorUsername;
    private final int rating; // 1-5
    private final String comment;
    private final String createdAt;

    public Review(String authorUsername, int rating, String comment) {
        this.authorUsername = authorUsername;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
