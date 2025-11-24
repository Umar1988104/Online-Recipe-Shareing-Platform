package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(RecipeRepository recipeRepository,
                          FavoriteRepository favoriteRepository,
                          ReviewRepository reviewRepository,
                          User currentUser) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome, " + currentUser.getUsername());
        AppTheme.styleHeading(title);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Dashboard overview");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        int totalRecipes = recipeRepository.getAllRecipes().size();
        int myRecipes = currentUser.isContributor() || currentUser.isAdmin()
                ? recipeRepository.getRecipesByAuthor(currentUser.getUsername()).size()
                : 0;
        int favorites = favoriteRepository.getFavorites(currentUser).size();
        int totalReviews = reviewRepository.getTotalReviewCount();

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 10));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cards.add(createStatCard("Total recipes", String.valueOf(totalRecipes)));
        cards.add(createStatCard("My recipes", String.valueOf(myRecipes)));
        cards.add(createStatCard("My favourites", String.valueOf(favorites)));
        cards.add(createStatCard("Total reviews", String.valueOf(totalReviews)));

        JPanel chartPanel = new PieChartPanel(totalRecipes, myRecipes, favorites, totalReviews);
        chartPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel mixPanel = new DataMixPanel(totalRecipes, myRecipes, favorites, totalReviews);
        mixPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        content.add(title);
        content.add(subtitle);
        content.add(cards);
        content.add(chartPanel);
        content.add(mixPanel);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 242, 250));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(valueLabel);
        panel.add(labelLabel);

        return panel;
    }

    /**
     * Simple pie chart using blue / grey / yellow / orange to show how much data there is.
     */
    private static class PieChartPanel extends JPanel {
        private final int totalRecipes;
        private final int myRecipes;
        private final int favourites;
        private final int totalReviews;

        public PieChartPanel(int totalRecipes, int myRecipes, int favourites, int totalReviews) {
            this.totalRecipes = totalRecipes;
            this.myRecipes = myRecipes;
            this.favourites = favourites;
            this.totalReviews = totalReviews;
            setPreferredSize(new Dimension(400, 180));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int[] values = { totalRecipes, myRecipes, favourites, totalReviews };
            String[] labels = { "Recipes", "Mine", "Favs", "Reviews" };
            Color[] colors = {
                    AppTheme.ACCENT,           // blue
                    AppTheme.CHART_GREY,       // grey
                    AppTheme.CHART_YELLOW,     // yellow
                    AppTheme.CHART_ORANGE      // orange
            };

            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 80;
            int xCenter = width / 3;
            int yCenter = height / 2;
            int radius = size / 2;

            int sum = 0;
            for (int v : values) sum += v;
            if (sum <= 0) sum = 1;

            int startAngle = 0;
            for (int i = 0; i < values.length; i++) {
                int angle = (int) Math.round(360.0 * values[i] / sum);
                g2.setColor(colors[i]);
                g2.fillArc(xCenter - radius, yCenter - radius, radius * 2, radius * 2,
                        startAngle, angle);
                startAngle += angle;
            }

            // Legend on the right
            int legendX = width - 180;
            int legendY = yCenter - 40;
            for (int i = 0; i < labels.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRect(legendX, legendY + i * 20, 14, 14);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(labels[i] + " (" + values[i] + ")", legendX + 20, legendY + 12 + i * 20);
            }

            g2.dispose();
        }
    }

    /**
     * Second chart: shows proportions of my data vs global in horizontal bars.
     */
    private static class DataMixPanel extends JPanel {
        private final int totalRecipes;
        private final int myRecipes;
        private final int favourites;
        private final int totalReviews;

        public DataMixPanel(int totalRecipes, int myRecipes, int favourites, int totalReviews) {
            this.totalRecipes = totalRecipes;
            this.myRecipes = myRecipes;
            this.favourites = favourites;
            this.totalReviews = totalReviews;
            setPreferredSize(new Dimension(400, 120));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth() - 60;
            int startX = 40;
            int barHeight = 16;
            int gap = 14;

            int y1 = 20;
            int y2 = y1 + barHeight + gap;
            int y3 = y2 + barHeight + gap;

            // Helper for drawing a labelled bar with two colors
            drawRatioBar(g2, "My recipes", myRecipes, totalRecipes, startX, y1, width,
                    AppTheme.ACCENT, AppTheme.CHART_GREY);
            drawRatioBar(g2, "My favourites", favourites, totalRecipes, startX, y2, width,
                    AppTheme.CHART_YELLOW, AppTheme.CHART_GREY);
            drawRatioBar(g2, "Reviews", totalReviews, Math.max(totalRecipes, totalReviews), startX, y3, width,
                    AppTheme.CHART_ORANGE, AppTheme.CHART_GREY);

            g2.dispose();
        }

        private void drawRatioBar(Graphics2D g2, String label, int part, int total,
                                  int x, int y, int width, Color partColor, Color restColor) {
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(label, x, y - 4);

            if (total <= 0) total = 1;
            double ratio = (double) part / total;
            int filled = (int) (ratio * width);

            g2.setColor(restColor);
            g2.fillRoundRect(x, y, width, 16, 8, 8);

            g2.setColor(partColor);
            g2.fillRoundRect(x, y, filled, 16, 8, 8);

            String valueText = part + "/" + total;
            int textWidth = g2.getFontMetrics().stringWidth(valueText);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(valueText, x + width - textWidth, y + 12);
        }
    }
}
