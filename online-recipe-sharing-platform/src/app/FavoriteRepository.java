package app;

import java.util.*;

public class FavoriteRepository {

    private final Map<String, List<Recipe>> favoritesByUser = new HashMap<>();

    public List<Recipe> getFavorites(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(favoritesByUser.getOrDefault(user.getUsername().toLowerCase(), Collections.emptyList()));
    }

    public boolean isFavorite(User user, Recipe recipe) {
        if (user == null || recipe == null) {
            return false;
        }
        List<Recipe> list = favoritesByUser.get(user.getUsername().toLowerCase());
        return list != null && list.contains(recipe);
    }

    public void addFavorite(User user, Recipe recipe) {
        if (user == null || recipe == null) {
            return;
        }
        String key = user.getUsername().toLowerCase();
        List<Recipe> list = favoritesByUser.computeIfAbsent(key, k -> new ArrayList<>());
        if (!list.contains(recipe)) {
            list.add(recipe);
        }
    }

    public void removeFavorite(User user, Recipe recipe) {
        if (user == null || recipe == null) {
            return;
        }
        String key = user.getUsername().toLowerCase();
        List<Recipe> list = favoritesByUser.get(key);
        if (list != null) {
            list.remove(recipe);
        }
    }
}
