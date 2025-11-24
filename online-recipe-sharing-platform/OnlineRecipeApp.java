import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Java Swing GUI for the Online Recipe Sharing Platform.
 *
 * This starter implementation keeps all data in memory and focuses on
 * demonstrating a working desktop GUI with two main roles:
 * - Recipe Explorer: browse recipes and see details
 * - Recipe Contributor: add new recipes
 *
 * You can compile and run this file with:
 *   javac OnlineRecipeApp.java
 *   java OnlineRecipeApp
 */
public class OnlineRecipeApp extends JFrame {

    private final RecipeRepository recipeRepository;

    public OnlineRecipeApp() {
        super("Online Recipe Sharing Platform");
        this.recipeRepository = new RecipeRepository();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // center on screen

        // Main content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Explorer", new ExplorerPanel(recipeRepository));
        tabbedPane.addTab("Contributor", new ContributorPanel(recipeRepository));

        // Admin dashboard is left as future work
        tabbedPane.addTab("Admin (stub)", createAdminStubPanel());

        setContentPane(tabbedPane);
    }

    private JPanel createAdminStubPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Admin dashboard will manage users, recipe approvals, and settings.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OnlineRecipeApp app = new OnlineRecipeApp();
            app.setVisible(true);
        });
    }

    // ===== Domain model =====

    static class Recipe {
        private final String title;
        private final String ingredients;
        private final String instructions;
        private int rating; // 0 means not rated yet

        public Recipe(String title, String ingredients, String instructions) {
            this.title = title;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.rating = 0;
        }

        public String getTitle() {
            return title;
        }

        public String getIngredients() {
            return ingredients;
        }

        public String getInstructions() {
            return instructions;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    /**
     * In-memory repository for recipes.
     * Later you can replace this with database or file storage.
     */
    static class RecipeRepository {
        private final List<Recipe> recipes = new ArrayList<>();

        public RecipeRepository() {
            // Seed with a few sample recipes
            addRecipe(new Recipe(
                    "Spaghetti Bolognese",
                    "Spaghetti, minced beef, tomato sauce, onions, garlic, olive oil, salt, pepper",
                    "1. Cook spaghetti.\n2. Brown minced beef with onions and garlic.\n3. Add tomato sauce and simmer.\n4. Serve sauce over spaghetti."
            ));
            addRecipe(new Recipe(
                    "Classic Pancakes",
                    "Flour, milk, eggs, sugar, baking powder, butter, salt",
                    "1. Mix dry ingredients.\n2. Whisk in milk and eggs.\n3. Cook batter on a greased pan until golden."
            ));
        }

        public List<Recipe> getAllRecipes() {
            return new ArrayList<>(recipes);
        }

        public void addRecipe(Recipe recipe) {
            recipes.add(recipe);
        }
    }

    // ===== Explorer UI =====

    static class ExplorerPanel extends JPanel {
        private final RecipeRepository recipeRepository;
        private final DefaultListModel<Recipe> listModel;
        private final JList<Recipe> recipeList;
        private final JTextArea ingredientsArea;
        private final JTextArea instructionsArea;
        private final JLabel ratingLabel;
        private final JComboBox<Integer> ratingComboBox;

        public ExplorerPanel(RecipeRepository recipeRepository) {
            super(new BorderLayout());
            this.recipeRepository = recipeRepository;

            listModel = new DefaultListModel<>();
            recipeList = new JList<>(listModel);
            recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ingredientsArea = new JTextArea();
            ingredientsArea.setEditable(false);
            ingredientsArea.setLineWrap(true);
            ingredientsArea.setWrapStyleWord(true);

            instructionsArea = new JTextArea();
            instructionsArea.setEditable(false);
            instructionsArea.setLineWrap(true);
            instructionsArea.setWrapStyleWord(true);

            ratingLabel = new JLabel("Rating: Not rated");
            ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

            buildUI();
            loadRecipes();
        }

        private void buildUI() {
            setBorder(new EmptyBorder(10, 10, 10, 10));

            // Left: list of recipes
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(new JLabel("Recipes"), BorderLayout.NORTH);
            leftPanel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
            leftPanel.setPreferredSize(new Dimension(250, 0));

            // Right: details
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

            JPanel ingredientsPanel = new JPanel(new BorderLayout());
            ingredientsPanel.add(new JLabel("Ingredients"), BorderLayout.NORTH);
            ingredientsPanel.add(new JScrollPane(ingredientsArea), BorderLayout.CENTER);
            ingredientsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

            JPanel instructionsPanel = new JPanel(new BorderLayout());
            instructionsPanel.add(new JLabel("Instructions"), BorderLayout.NORTH);
            instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);
            instructionsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

            JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            ratingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            ratingPanel.add(ratingLabel);
            ratingPanel.add(new JLabel("  |  Set rating:"));
            ratingPanel.add(ratingComboBox);
            JButton applyRatingButton = new JButton("Apply");
            ratingPanel.add(applyRatingButton);

            rightPanel.add(ingredientsPanel);
            rightPanel.add(instructionsPanel);
            rightPanel.add(ratingPanel);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            splitPane.setResizeWeight(0.3);

            add(splitPane, BorderLayout.CENTER);

            // List selection handling
            recipeList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateDetailsFromSelection();
                }
            });

            // Rating handling
            applyRatingButton.addActionListener(e -> applyRating());
        }

        private void loadRecipes() {
            listModel.clear();
            for (Recipe r : recipeRepository.getAllRecipes()) {
                listModel.addElement(r);
            }
            if (!listModel.isEmpty()) {
                recipeList.setSelectedIndex(0);
            }
        }

        private void updateDetailsFromSelection() {
            Recipe selected = recipeList.getSelectedValue();
            if (selected == null) {
                ingredientsArea.setText("");
                instructionsArea.setText("");
                ratingLabel.setText("Rating: Not rated");
                return;
            }
            ingredientsArea.setText(selected.getIngredients());
            instructionsArea.setText(selected.getInstructions());
            int rating = selected.getRating();
            if (rating <= 0) {
                ratingLabel.setText("Rating: Not rated");
            } else {
                ratingLabel.setText("Rating: " + rating + "/5");
            }
        }

        private void applyRating() {
            Recipe selected = recipeList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a recipe first.",
                        "No recipe selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Integer rating = (Integer) ratingComboBox.getSelectedItem();
            if (rating != null) {
                selected.setRating(rating);
                updateDetailsFromSelection();
            }
        }
    }

    // ===== Contributor UI =====

    static class ContributorPanel extends JPanel {
        private final RecipeRepository recipeRepository;
        private final DefaultListModel<Recipe> listModel;
        private final JList<Recipe> recipeList;

        private final JTextField titleField;
        private final JTextArea ingredientsArea;
        private final JTextArea instructionsArea;

        public ContributorPanel(RecipeRepository recipeRepository) {
            super(new BorderLayout());
            this.recipeRepository = recipeRepository;

            listModel = new DefaultListModel<>();
            recipeList = new JList<>(listModel);
            recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            titleField = new JTextField();
            ingredientsArea = new JTextArea(4, 20);
            instructionsArea = new JTextArea(6, 20);

            ingredientsArea.setLineWrap(true);
            ingredientsArea.setWrapStyleWord(true);
            instructionsArea.setLineWrap(true);
            instructionsArea.setWrapStyleWord(true);

            buildUI();
            loadRecipes();
        }

        private void buildUI() {
            setBorder(new EmptyBorder(10, 10, 10, 10));

            // Left: existing recipes (owned by this contributor in a real app; here we show all)
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(new JLabel("Existing Recipes"), BorderLayout.NORTH);
            leftPanel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
            leftPanel.setPreferredSize(new Dimension(250, 0));

            // Right: recipe form
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.add(new JLabel("Title"), BorderLayout.NORTH);
            titlePanel.add(titleField, BorderLayout.CENTER);
            titlePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

            JPanel ingredientsPanel = new JPanel(new BorderLayout());
            ingredientsPanel.add(new JLabel("Ingredients"), BorderLayout.NORTH);
            ingredientsPanel.add(new JScrollPane(ingredientsArea), BorderLayout.CENTER);
            ingredientsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

            JPanel instructionsPanel = new JPanel(new BorderLayout());
            instructionsPanel.add(new JLabel("Instructions"), BorderLayout.NORTH);
            instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);
            instructionsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

            JButton addButton = new JButton("Add Recipe");
            addButton.addActionListener(new AddRecipeAction());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            buttonPanel.add(addButton);

            formPanel.add(titlePanel);
            formPanel.add(ingredientsPanel);
            formPanel.add(instructionsPanel);
            formPanel.add(buttonPanel);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, formPanel);
            splitPane.setResizeWeight(0.3);

            add(splitPane, BorderLayout.CENTER);
        }

        private void loadRecipes() {
            listModel.clear();
            for (Recipe r : recipeRepository.getAllRecipes()) {
                listModel.addElement(r);
            }
        }

        private class AddRecipeAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText().trim();
                String ingredients = ingredientsArea.getText().trim();
                String instructions = instructionsArea.getText().trim();

                if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
                    JOptionPane.showMessageDialog(ContributorPanel.this,
                            "Please fill in title, ingredients, and instructions.",
                            "Missing information",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Recipe recipe = new Recipe(title, ingredients, instructions);
                recipeRepository.addRecipe(recipe);
                listModel.addElement(recipe);

                // Clear form
                titleField.setText("");
                ingredientsArea.setText("");
                instructionsArea.setText("");

                JOptionPane.showMessageDialog(ContributorPanel.this,
                        "Recipe added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
