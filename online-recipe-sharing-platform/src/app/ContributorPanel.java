package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContributorPanel extends JPanel {
    private final RecipeRepository recipeRepository;
    private final User currentUser;
    private final ActivityRepository activityRepository;
    private final DefaultListModel<Recipe> listModel;
    private final JList<Recipe> recipeList;

    private final JTextField titleField;
    private final JTextArea ingredientsArea;
    private final JTextArea instructionsArea;

    public ContributorPanel(RecipeRepository recipeRepository, User currentUser, ActivityRepository activityRepository) {
        super(new BorderLayout());
        this.recipeRepository = recipeRepository;
        this.currentUser = currentUser;
        this.activityRepository = activityRepository;

        listModel = new DefaultListModel<>();
        recipeList = new JList<>(listModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        titleField = new JTextField();
        titleField.setToolTipText("Enter a clear, descriptive recipe title");
        ingredientsArea = new JTextArea(4, 20);
        ingredientsArea.setToolTipText("List ingredients, separated by commas or new lines");
        instructionsArea = new JTextArea(6, 20);
        instructionsArea.setToolTipText("Write step-by-step cooking instructions");

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
        JLabel existingLabel = new JLabel("Existing Recipes");
        AppTheme.styleHeading(existingLabel);
        leftPanel.add(existingLabel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(250, 0));

        // Right: recipe form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Title");
        AppTheme.styleHeading(titleLabel);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        titlePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JLabel ingredientsLabel = new JLabel("Ingredients");
        AppTheme.styleHeading(ingredientsLabel);
        JPanel ingredientsPanel = new JPanel(new BorderLayout());
        ingredientsPanel.add(ingredientsLabel, BorderLayout.NORTH);
        ingredientsPanel.add(new JScrollPane(ingredientsArea), BorderLayout.CENTER);
        ingredientsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JLabel instructionsLabel = new JLabel("Instructions");
        AppTheme.styleHeading(instructionsLabel);
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.add(instructionsLabel, BorderLayout.NORTH);
        instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);
        instructionsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JButton newButton = new JButton("New");
        newButton.setToolTipText("Start creating a new recipe");
        newButton.addActionListener(e -> clearForm());

        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Save this recipe (new or updated)");
        saveButton.addActionListener(new SaveRecipeAction());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);

        formPanel.add(titlePanel);
        formPanel.add(ingredientsPanel);
        formPanel.add(instructionsPanel);
        formPanel.add(buttonPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, formPanel);
        splitPane.setResizeWeight(0.3);

        add(splitPane, BorderLayout.CENTER);

        // Load data and hook selection
        loadRecipes();
        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRecipeSelected();
            }
        });
    }

    private void loadRecipes() {
        listModel.clear();
        for (Recipe r : recipeRepository.getRecipesByAuthor(currentUser.getUsername())) {
            listModel.addElement(r);
        }
    }

    private void onRecipeSelected() {
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            clearForm();
            return;
        }
        titleField.setText(selected.getTitle());
        ingredientsArea.setText(selected.getIngredients());
        instructionsArea.setText(selected.getInstructions());
    }

    private void clearForm() {
        recipeList.clearSelection();
        titleField.setText("");
        ingredientsArea.setText("");
        instructionsArea.setText("");
    }

    private class SaveRecipeAction implements ActionListener {
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

            Recipe selected = recipeList.getSelectedValue();
            if (selected == null) {
                // New recipe owned by this contributor
                Recipe recipe = new Recipe(title, ingredients, instructions, currentUser.getUsername(), false);
                recipeRepository.addRecipe(recipe);
                listModel.addElement(recipe);
                recipeList.setSelectedValue(recipe, true);
                if (activityRepository != null) {
                    activityRepository.add(currentUser.getUsername(),
                            ActivityEntry.Type.RECIPE_CREATED,
                            "Created recipe: " + title);
                }
                JOptionPane.showMessageDialog(ContributorPanel.this,
                        "Recipe created.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Edit existing recipe (owned by this contributor)
                selected.setTitle(title);
                selected.setIngredients(ingredients);
                selected.setInstructions(instructions);
                recipeList.repaint();
                if (activityRepository != null) {
                    activityRepository.add(currentUser.getUsername(),
                            ActivityEntry.Type.RECIPE_UPDATED,
                            "Updated recipe: " + title);
                }
                JOptionPane.showMessageDialog(ContributorPanel.this,
                        "Recipe updated.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
