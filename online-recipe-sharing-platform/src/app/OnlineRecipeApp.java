package app;

import javax.swing.*;
import java.awt.*;

public class OnlineRecipeApp extends JFrame {

    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final ActivityRepository activityRepository;
    private final User currentUser;
    private final JTabbedPane tabbedPane;

    public OnlineRecipeApp(User currentUser) {
        super("Online Recipe Sharing Platform");
        this.currentUser = currentUser;
        this.recipeRepository = new RecipeRepository();
        this.favoriteRepository = new FavoriteRepository();
        this.reviewRepository = new ReviewRepository();
        this.activityRepository = new ActivityRepository();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // center on screen

        setJMenuBar(createMenuBar());

        tabbedPane = new JTabbedPane();
        configureTabsForRole();

        setContentPane(tabbedPane);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            // Restart login flow
            main(new String[0]);
        });
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Online Recipe Sharing Platform\n" +
                        "Roles: Admin, Contributor, Explorer\n" +
                        "Demo build with Java Swing.",
                "About",
                JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void configureTabsForRole() {
        tabbedPane.removeAll();

        // High-level dashboard with charts
        tabbedPane.addTab("Dashboard", new DashboardPanel(recipeRepository, favoriteRepository, reviewRepository, currentUser));

        // All roles can explore recipes
        tabbedPane.addTab("Explorer", new ExplorerPanel(recipeRepository, currentUser, favoriteRepository, reviewRepository, activityRepository));

        if (currentUser.isContributor() || currentUser.isAdmin()) {
            tabbedPane.addTab("My Recipes", new ContributorPanel(recipeRepository, currentUser, activityRepository));
        }

        // Personal favourites collection for all roles
        tabbedPane.addTab("My Collection", new FavoritesPanel(favoriteRepository, currentUser));

        if (currentUser.isAdmin()) {
            tabbedPane.addTab("Admin", new AdminPanel(recipeRepository, activityRepository));
        }

        tabbedPane.addTab("Profile", createProfilePanel());
    }

    private JPanel createProfilePanel() {
        return new ProfilePanel(currentUser, recipeRepository, favoriteRepository, reviewRepository, activityRepository);
    }

    public static void main(String[] args) {
        AppTheme.applyLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            UserRepository userRepository = new UserRepository();
            LoginDialog loginDialog = new LoginDialog(null, userRepository);
            User user = loginDialog.showDialog();
            if (user == null) {
                // User cancelled or failed login
                System.exit(0);
            }

            OnlineRecipeApp app = new OnlineRecipeApp(user);
            app.setVisible(true);
        });
    }
}
