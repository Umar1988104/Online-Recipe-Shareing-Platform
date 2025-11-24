package app;

import javax.swing.*;
import java.awt.*;

public class AppTheme {

    public static final Color ACCENT = new Color(76, 110, 245);      // blue
    public static final Color ACCENT_DARK = new Color(46, 80, 210); // dark blue
    public static final Color CHART_GREY = new Color(180, 185, 195);
    public static final Color CHART_YELLOW = new Color(255, 204, 0);
    public static final Color CHART_ORANGE = new Color(255, 153, 0);

    public static void applyLookAndFeel() {
        try {
            // Prefer Nimbus if available
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fallback: default LAF
        }

        // Subtle brand-style tweaks
        UIManager.put("control", new Color(245, 246, 250));
        UIManager.put("info", new Color(245, 246, 250));
        UIManager.put("nimbusBase", new Color(40, 45, 80));
        UIManager.put("nimbusBlueGrey", new Color(190, 195, 210));
        UIManager.put("text", Color.DARK_GRAY);
    }

    public static void styleHeading(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        label.setForeground(ACCENT_DARK);
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(230, 232, 240));
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
    }
}
