package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    public ProfilePanel(User currentUser,
                        RecipeRepository recipeRepository,
                        FavoriteRepository favoriteRepository,
                        ReviewRepository reviewRepository,
                        ActivityRepository activityRepository) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Header row with name + role badge
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerRow.setOpaque(false);

        JLabel nameLabel = new JLabel(currentUser.getUsername());
        AppTheme.styleHeading(nameLabel);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));

        JLabel roleBadge = new JLabel(currentUser.getRole().name());
        roleBadge.setOpaque(true);
        roleBadge.setBorder(new EmptyBorder(2, 8, 2, 8));
        roleBadge.setForeground(Color.WHITE);
        switch (currentUser.getRole()) {
            case ADMIN:
                roleBadge.setBackground(AppTheme.CHART_ORANGE); // orange
                break;
            case CONTRIBUTOR:
                roleBadge.setBackground(AppTheme.ACCENT); // blue
                break;
            case EXPLORER:
            default:
                roleBadge.setBackground(AppTheme.CHART_GREY); // grey
                break;
        }

        headerRow.add(nameLabel);
        headerRow.add(roleBadge);

        // Stats section
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        int myRecipes = currentUser.isContributor() || currentUser.isAdmin()
                ? recipeRepository.getRecipesByAuthor(currentUser.getUsername()).size()
                : 0;
        int myFavourites = favoriteRepository.getFavorites(currentUser).size();
        int myReviews = reviewRepository.getReviewCountByUser(currentUser.getUsername());

        statsPanel.add(createStatChip("My recipes", String.valueOf(myRecipes), AppTheme.ACCENT));
        statsPanel.add(createStatChip("My favourites", String.valueOf(myFavourites), AppTheme.CHART_YELLOW));
        statsPanel.add(createStatChip("My reviews", String.valueOf(myReviews), AppTheme.CHART_ORANGE));

        // Preferences section
        JPanel prefsPanel = new JPanel();
        prefsPanel.setLayout(new BoxLayout(prefsPanel, BoxLayout.Y_AXIS));
        prefsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        prefsPanel.setOpaque(false);

        JLabel prefsHeading = new JLabel("Preferences");
        AppTheme.styleHeading(prefsHeading);
        prefsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox darkAccent = new JCheckBox("Use darker accent bars (demo only)");
        darkAccent.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox emailNotif = new JCheckBox("Email notifications for recipe approvals (demo only)");
        emailNotif.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveButton = new JButton("Save Preferences");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Preferences saved (not persisted in this demo).",
                "Profile",
                JOptionPane.INFORMATION_MESSAGE));

        prefsPanel.add(prefsHeading);
        prefsPanel.add(Box.createVerticalStrut(5));
        prefsPanel.add(darkAccent);
        prefsPanel.add(emailNotif);
        prefsPanel.add(Box.createVerticalStrut(10));
        prefsPanel.add(saveButton);

        // Recent activity section
        JPanel activityPanel = new JPanel();
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        activityPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        activityPanel.setOpaque(false);

        JLabel activityHeading = new JLabel("Recent activity");
        AppTheme.styleHeading(activityHeading);
        activityHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        java.util.List<ActivityEntry> recent = activityRepository.getRecentForUser(currentUser.getUsername(), 10);
        DefaultListModel<String> model = new DefaultListModel<>();
        for (ActivityEntry e : recent) {
            model.addElement(e.getTimestamp() + " - " + e.getType() + ": " + e.getDescription());
        }
        JList<String> activityList = new JList<>(model);
        JScrollPane activityScroll = new JScrollPane(activityList);
        activityScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        activityScroll.setPreferredSize(new Dimension(400, 140));

        activityPanel.add(activityHeading);
        activityPanel.add(Box.createVerticalStrut(5));
        activityPanel.add(activityScroll);

        content.add(headerRow);
        content.add(statsPanel);
        content.add(prefsPanel);
        content.add(activityPanel);

        add(content, BorderLayout.NORTH);
    }

    private JPanel createStatChip(String label, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 20f));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(valueLabel);
        panel.add(labelLabel);
        return panel;
    }
}
