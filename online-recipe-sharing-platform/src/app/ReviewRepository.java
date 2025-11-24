package app;

import java.util.*;

public class ReviewRepository {

    private final Map<Recipe, List<Review>> reviewsByRecipe = new HashMap<>();

    public List<Review> getReviews(Recipe recipe) {
        if (recipe == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(reviewsByRecipe.getOrDefault(recipe, Collections.emptyList()));
    }

    public void addReview(Recipe recipe, Review review) {
        if (recipe == null || review == null) {
            return;
        }
        List<Review> list = reviewsByRecipe.computeIfAbsent(recipe, r -> new ArrayList<>());
        list.add(review);
    }

    public int getTotalReviewCount() {
        int count = 0;
        for (List<Review> list : reviewsByRecipe.values()) {
            count += list.size();
        }
        return count;
    }

    public int getReviewCountByUser(String username) {
        if (username == null) return 0;
        int count = 0;
        for (List<Review> list : reviewsByRecipe.values()) {
            for (Review r : list) {
                if (username.equalsIgnoreCase(r.getAuthorUsername())) {
                    count++;
                }
            }
        }
        return count;
    }

    public double getAverageRating(Recipe recipe) {
        List<Review> list = reviewsByRecipe.get(recipe);
        if (list == null || list.isEmpty()) {
            return -1;
        }
        int sum = 0;
        for (Review r : list) {
            sum += r.getRating();
        }
        return (double) sum / list.size();
    }
}
