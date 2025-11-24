package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminPanel extends JPanel {

    private final RecipeRepository recipeRepository;
    private final ActivityRepository activityRepository;

    private DefaultListModel<Recipe> recipeListModel;
    private JList<Recipe> recipeList;
    private JTextField titleField;
    private JTextArea ingredientsArea;
    private JTextArea instructionsArea;
    private JCheckBox approvedCheckBox;

    public AdminPanel(RecipeRepository recipeRepository, ActivityRepository activityRepository) {
        super(new BorderLayout());
        this.recipeRepository = recipeRepository;
        this.activityRepository = activityRepository;

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Recipes", createRecipesPanel());
        tabs.addTab("Settings", createSettingsPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createRecipesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        recipeListModel = new DefaultListModel<>();
        recipeList = new JList<>(recipeListModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel left = new JPanel(new BorderLayout());
        JLabel listLabel = new JLabel("All Recipes");
        AppTheme.styleHeading(listLabel);
        left.add(listLabel, BorderLayout.NORTH);
        left.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(260, 0));

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        titleField = new JTextField();
        ingredientsArea = new JTextArea(4, 20);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        instructionsArea = new JTextArea(6, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        approvedCheckBox = new JCheckBox("Approved");

        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Title");
        AppTheme.styleHeading(titleLabel);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        titlePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel ingredientsPanel = new JPanel(new BorderLayout());
        JLabel ingredientsLabel = new JLabel("Ingredients");
        AppTheme.styleHeading(ingredientsLabel);
        ingredientsPanel.add(ingredientsLabel, BorderLayout.NORTH);
        ingredientsPanel.add(new JScrollPane(ingredientsArea), BorderLayout.CENTER);
        ingredientsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel instructionsPanel = new JPanel(new BorderLayout());
        JLabel instructionsLabel = new JLabel("Instructions");
        AppTheme.styleHeading(instructionsLabel);
        instructionsPanel.add(instructionsLabel, BorderLayout.NORTH);
        instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);
        instructionsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        statusPanel.add(approvedCheckBox);

        JButton newButton = new JButton("New");
        newButton.setToolTipText("Clear form to create a new recipe as admin");
        newButton.addActionListener(e -> clearForm());
        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Save recipe changes (including approval)");
        saveButton.addActionListener(e -> saveRecipe());
        JButton deleteButton = new JButton("Delete");
        deleteButton.setToolTipText("Delete the selected recipe");
        deleteButton.addActionListener(e -> deleteRecipe());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        right.add(titlePanel);
        right.add(ingredientsPanel);
        right.add(instructionsPanel);
        right.add(statusPanel);
        right.add(buttonPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setResizeWeight(0.35);

        panel.add(splitPane, BorderLayout.CENTER);

        loadRecipes();

        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRecipeSelected();
            }
        });

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Application Settings");
        AppTheme.styleHeading(heading);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox maintenanceMode = new JCheckBox("Maintenance mode (demo only)");
        maintenanceMode.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox darkMode = new JCheckBox("Enable dark accent theme (demo only)");
        darkMode.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveButton = new JButton("Save Settings");
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(panel,
                "Settings saved (not persisted in this demo).",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE));

        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));
        panel.add(maintenanceMode);
        panel.add(darkMode);
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveButton);

        return panel;
    }

    private void loadRecipes() {
        recipeListModel.clear();
        for (Recipe recipe : recipeRepository.getAllRecipes()) {
            recipeListModel.addElement(recipe);
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
        approvedCheckBox.setSelected(selected.isApproved());
    }

    private void clearForm() {
        recipeList.clearSelection();
        titleField.setText("");
        ingredientsArea.setText("");
        instructionsArea.setText("");
        approvedCheckBox.setSelected(false);
    }

    private void saveRecipe() {
        String title = titleField.getText().trim();
        String ingredients = ingredientsArea.getText().trim();
        String instructions = instructionsArea.getText().trim();

        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in title, ingredients, and instructions.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            // New recipe created by admin and approved by default flag
            Recipe recipe = new Recipe(title, ingredients, instructions, "admin", approvedCheckBox.isSelected());
            recipeRepository.addRecipe(recipe);
            recipeListModel.addElement(recipe);
            recipeList.setSelectedValue(recipe, true);
            if (activityRepository != null) {
                activityRepository.add("admin", ActivityEntry.Type.RECIPE_CREATED,
                        "Admin created recipe: " + title);
            }
        } else {
            selected.setTitle(title);
            selected.setIngredients(ingredients);
            selected.setInstructions(instructions);
            selected.setApproved(approvedCheckBox.isSelected());
            recipeList.repaint();
            if (activityRepository != null) {
                activityRepository.add("admin", ActivityEntry.Type.RECIPE_UPDATED,
                        "Admin updated recipe: " + title);
            }
        }

        JOptionPane.showMessageDialog(this,
                "Recipe saved.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRecipe() {
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a recipe to delete.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete this recipe?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        recipeRepository.removeRecipe(selected);
        recipeListModel.removeElement(selected);
        clearForm();
        if (activityRepository != null) {
            activityRepository.add("admin", ActivityEntry.Type.RECIPE_DELETED,
                    "Admin deleted recipe: " + selected.getTitle());
        }
    }
}
