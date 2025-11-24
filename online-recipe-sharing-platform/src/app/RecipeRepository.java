package app;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {
    private final List<Recipe> recipes = new ArrayList<>();

    public RecipeRepository() {
        // Seed with a few sample recipes owned by admin and already approved
        addRecipe(new Recipe(
                "Spaghetti Bolognese",
                "Spaghetti, minced beef, tomato sauce, onions, garlic, olive oil, salt, pepper",
                "1. Cook spaghetti.\n2. Brown minced beef with onions and garlic.\n3. Add tomato sauce and simmer.\n4. Serve sauce over spaghetti.",
                "admin",
                true
        ));
        addRecipe(new Recipe(
                "Classic Pancakes",
                "Flour, milk, eggs, sugar, baking powder, butter, salt",
                "1. Mix dry ingredients.\n2. Whisk in milk and eggs.\n3. Cook batter on a greased pan until golden.",
                "admin",
                true
        ));
    }

    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }

    public List<Recipe> getRecipesByAuthor(String username) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getAuthorUsername().equalsIgnoreCase(username)) {
                result.add(recipe);
            }
        }
        return result;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }
}
