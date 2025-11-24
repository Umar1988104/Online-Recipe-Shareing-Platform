package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FavoritesPanel extends JPanel {
    private final FavoriteRepository favoriteRepository;
    private final User currentUser;
    private final DefaultListModel<Recipe> listModel;
    private final JList<Recipe> recipeList;
    private final JTextArea ingredientsArea;
    private final JTextArea instructionsArea;

    public FavoritesPanel(FavoriteRepository favoriteRepository, User currentUser) {
        super(new BorderLayout());
        this.favoriteRepository = favoriteRepository;
        this.currentUser = currentUser;

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

        buildUI();
        loadFavorites();
    }

    private void buildUI() {
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel left = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("My Favourites");
        heading.setToolTipText("Recipes you have marked as favourites");
        AppTheme.styleHeading(heading);
        left.add(heading, BorderLayout.NORTH);
        left.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(250, 0));

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

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

        right.add(ingredientsPanel);
        right.add(instructionsPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setResizeWeight(0.3);

        add(splitPane, BorderLayout.CENTER);

        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onSelectionChanged();
            }
        });
    }

    public void loadFavorites() {
        listModel.clear();
        for (Recipe r : favoriteRepository.getFavorites(currentUser)) {
            listModel.addElement(r);
        }
        if (!listModel.isEmpty()) {
            recipeList.setSelectedIndex(0);
        }
    }

    private void onSelectionChanged() {
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            ingredientsArea.setText("");
            instructionsArea.setText("");
            return;
        }
        ingredientsArea.setText(selected.getIngredients());
        instructionsArea.setText(selected.getInstructions());
    }
}
