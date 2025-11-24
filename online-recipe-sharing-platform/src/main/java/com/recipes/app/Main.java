package com.recipes.app;

import app.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Apply theme
        AppTheme.applyLookAndFeel();

        // Initialize repositories
        UserRepository userRepository = new UserRepository();
        RecipeRepository recipeRepository = new RecipeRepository();

        // Show login dialog
        JFrame tempFrame = new JFrame();
        tempFrame.setVisible(false);
        LoginDialog loginDialog = new LoginDialog(tempFrame, userRepository);
        User user = loginDialog.showDialog();
        tempFrame.dispose();

        if (user == null) {
            // User cancelled login
            System.exit(0);
            return;
        }

        // Create main frame
        JFrame frame = new JFrame("Online Recipe Sharing Platform - " + user.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // Route to appropriate dashboard based on role
        JPanel dashboardPanel;
        if (user.isAdmin()) {
            dashboardPanel = new AdminPanel(recipeRepository);
        } else if (user.isContributor()) {
            dashboardPanel = new ContributorPanel(recipeRepository, user);
        } else if (user.isExplorer()) {
            dashboardPanel = new ExplorerPanel(recipeRepository);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Unknown user role", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        frame.setContentPane(dashboardPanel);
        frame.setVisible(true);
    }
}
