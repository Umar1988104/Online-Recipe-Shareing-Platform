package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityRepository {

    private final List<ActivityEntry> entries = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public synchronized void add(String username, ActivityEntry.Type type, String description) {
        String ts = LocalDateTime.now().format(formatter);
        entries.add(new ActivityEntry(username, type, description, ts));
    }

    public synchronized List<ActivityEntry> getRecentForUser(String username, int max) {
        if (username == null) {
            return Collections.emptyList();
        }
        List<ActivityEntry> result = new ArrayList<>();
        for (int i = entries.size() - 1; i >= 0 && result.size() < max; i--) {
            ActivityEntry e = entries.get(i);
            if (username.equalsIgnoreCase(e.getUsername())) {
                result.add(e);
            }
        }
        Collections.reverse(result);
        return result;
    }
}