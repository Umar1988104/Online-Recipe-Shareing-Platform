package app;

public class Recipe {
    private String title;
    private String ingredients;
    private String instructions;
    private int rating; // 0 means not rated yet
    private final String authorUsername;
    private boolean approved;

    public Recipe(String title, String ingredients, String instructions, String authorUsername, boolean approved) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.authorUsername = authorUsername;
        this.approved = approved;
        this.rating = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isOwnedBy(User user) {
        return user != null && user.getUsername().equalsIgnoreCase(authorUsername);
    }

    @Override
    public String toString() {
        return title + " (by " + authorUsername + (approved ? ", approved" : ", pending") + ")";
    }
}
