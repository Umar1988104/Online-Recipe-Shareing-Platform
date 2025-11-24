package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ExplorerPanel extends JPanel {
    private final RecipeRepository recipeRepository;
    private final User currentUser;
    private final FavoriteRepository favoriteRepository;
    private final DefaultListModel<Recipe> listModel;
    private final JList<Recipe> recipeList;
    private final JTextArea ingredientsArea;
    private final JTextArea instructionsArea;
    private final JLabel ratingLabel;
    private final JComboBox<Integer> ratingComboBox;
    private final JTextField searchField;
    private final JCheckBox approvedOnlyCheckBox;
    private final JLabel favouriteBadge;

    private final ReviewRepository reviewRepository;
    private final ActivityRepository activityRepository;
    private final ReviewTableModel reviewTableModel;
    private final JTable reviewTable;
    private final JComboBox<Integer> reviewRatingComboBox;
    private final JTextArea reviewCommentArea;

    public ExplorerPanel(RecipeRepository recipeRepository,
                         User currentUser,
                         FavoriteRepository favoriteRepository,
                         ReviewRepository reviewRepository,
                         ActivityRepository activityRepository) {
        super(new BorderLayout());
        this.recipeRepository = recipeRepository;
        this.currentUser = currentUser;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.activityRepository = activityRepository;

        listModel = new DefaultListModel<>();
        recipeList = new JList<>(listModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        searchField = new JTextField();
        approvedOnlyCheckBox = new JCheckBox("Approved only");
        approvedOnlyCheckBox.setSelected(true);

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
        favouriteBadge = new JLabel(" ");

        reviewTableModel = new ReviewTableModel();
        reviewTable = new JTable(reviewTableModel);
        reviewRatingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        reviewCommentArea = new JTextArea(3, 20);
        reviewCommentArea.setLineWrap(true);
        reviewCommentArea.setWrapStyleWord(true);

        buildUI();
        loadRecipes();
    }

    private void buildUI() {
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left: search + list of recipes
        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel listLabel = new JLabel("Recipes");
        AppTheme.styleHeading(listLabel);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Search");
        AppTheme.styleSecondaryButton(searchButton);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel leftTop = new JPanel();
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.add(listLabel);
        leftTop.add(searchPanel);
        leftTop.add(approvedOnlyCheckBox);

        leftPanel.add(leftTop, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(260, 0));

        // Right: details
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

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

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        ratingPanel.add(ratingLabel);
        ratingPanel.add(favouriteBadge);
        ratingPanel.add(new JLabel("  |  Set rating:"));
        ratingPanel.add(ratingComboBox);
        JButton applyRatingButton = new JButton("Apply");
        AppTheme.stylePrimaryButton(applyRatingButton);
        ratingPanel.add(applyRatingButton);

        JButton favToggleButton = new JButton("Add to favourites");
        AppTheme.styleSecondaryButton(favToggleButton);
        ratingPanel.add(favToggleButton);

        // Reviews section
        JLabel reviewsLabel = new JLabel("Reviews");
        AppTheme.styleHeading(reviewsLabel);
        JPanel reviewsPanel = new JPanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JScrollPane tableScroll = new JScrollPane(reviewTable);
        tableScroll.setPreferredSize(new Dimension(400, 120));

        JPanel addReviewPanel = new JPanel(new BorderLayout(5, 5));
        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingRow.add(new JLabel("Rating:"));
        ratingRow.add(reviewRatingComboBox);
        JButton addReviewButton = new JButton("Add Review");
        AppTheme.stylePrimaryButton(addReviewButton);
        ratingRow.add(addReviewButton);

        addReviewPanel.add(ratingRow, BorderLayout.NORTH);
        addReviewPanel.add(new JScrollPane(reviewCommentArea), BorderLayout.CENTER);

        reviewsPanel.add(reviewsLabel);
        reviewsPanel.add(tableScroll);
        reviewsPanel.add(addReviewPanel);

        rightPanel.add(ingredientsPanel);
        rightPanel.add(instructionsPanel);
        rightPanel.add(ratingPanel);
        rightPanel.add(reviewsPanel);

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

        // Search and filter handling
        searchButton.addActionListener(e -> loadRecipes());
        approvedOnlyCheckBox.addActionListener(e -> loadRecipes());

        // Favourites handling
        favToggleButton.addActionListener(e -> toggleFavourite());

        // Add review handling
        addReviewButton.addActionListener(e -> addReview());
    }

    private void loadRecipes() {
        listModel.clear();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        boolean approvedOnly = approvedOnlyCheckBox.isSelected() && !currentUser.isAdmin();

        for (Recipe r : recipeRepository.getAllRecipes()) {
            if (approvedOnly && !r.isApproved()) {
                continue;
            }
            if (!query.isEmpty()) {
                String text = (r.getTitle() + " " + r.getIngredients()).toLowerCase();
                if (!text.contains(query)) {
                    continue;
                }
            }
            listModel.addElement(r);
        }

        if (!listModel.isEmpty()) {
            recipeList.setSelectedIndex(0);
        } else {
            ingredientsArea.setText("");
            instructionsArea.setText("");
            ratingLabel.setText("Rating: Not rated");
            favouriteBadge.setText(" ");
            reviewTableModel.setReviews(java.util.Collections.emptyList());
        }
    }

    private void updateDetailsFromSelection() {
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            ingredientsArea.setText("");
            instructionsArea.setText("");
            ratingLabel.setText("Rating: Not rated");
            reviewTableModel.setReviews(java.util.Collections.emptyList());
            return;
        }
        ingredientsArea.setText(selected.getIngredients());
        instructionsArea.setText(selected.getInstructions());

        java.util.List<Review> reviews = reviewRepository.getReviews(selected);
        reviewTableModel.setReviews(reviews);
        double average = reviewRepository.getAverageRating(selected);
        if (average < 0) {
            ratingLabel.setText("Rating: Not rated");
        } else {
            ratingLabel.setText(String.format("Average rating: %.1f/5 (%d reviews)", average, reviews.size()));
        }

        boolean isFav = favoriteRepository.isFavorite(currentUser, selected);
        favouriteBadge.setText(isFav ? "  ★ Favourite" : "");
        favouriteBadge.setForeground(isFav ? AppTheme.ACCENT_DARK : Color.DARK_GRAY);
    }

    private void applyRating() {
        // Keep for compatibility: directly set a quick rating without comment
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe first.",
                    "No recipe selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer rating = (Integer) ratingComboBox.getSelectedItem();
        if (rating != null) {
            Review quickReview = new Review(currentUser.getUsername(), rating, "");
            reviewRepository.addReview(selected, quickReview);
            updateDetailsFromSelection();
        }
    }

    private void addReview() {
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe first.",
                    "No recipe selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer rating = (Integer) reviewRatingComboBox.getSelectedItem();
        if (rating == null) {
            JOptionPane.showMessageDialog(this, "Please choose a rating.",
                    "Missing rating", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String comment = reviewCommentArea.getText().trim();
        Review review = new Review(currentUser.getUsername(), rating, comment);
        reviewRepository.addReview(selected, review);
        if (activityRepository != null) {
            activityRepository.add(currentUser.getUsername(), ActivityEntry.Type.REVIEW_ADDED,
                    "Reviewed recipe: " + selected.getTitle());
        }
        reviewCommentArea.setText("");
        updateDetailsFromSelection();
    }

    private void toggleFavourite() {
        Recipe selected = recipeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe first.",
                    "No recipe selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean wasFav = favoriteRepository.isFavorite(currentUser, selected);
        if (wasFav) {
            favoriteRepository.removeFavorite(currentUser, selected);
        } else {
            favoriteRepository.addFavorite(currentUser, selected);
        }
        if (activityRepository != null) {
            activityRepository.add(currentUser.getUsername(), ActivityEntry.Type.FAVOURITE_TOGGLED,
                    (wasFav ? "Removed from" : "Added to") + " favourites: " + selected.getTitle());
        }
        updateDetailsFromSelection();
    }

    private static class ReviewTableModel extends javax.swing.table.AbstractTableModel {
        private java.util.List<Review> reviews = new java.util.ArrayList<>();
        private final String[] columns = {"User", "Rating", "Comment", "Date"};

        public void setReviews(java.util.List<Review> reviews) {
            this.reviews = new java.util.ArrayList<>(reviews);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return reviews.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Review r = reviews.get(rowIndex);
            switch (columnIndex) {
                case 0: return r.getAuthorUsername();
                case 1: return r.getRating();
                case 2: return r.getComment();
                case 3: return r.getCreatedAt();
                default: return "";
            }
        }
    }
}
