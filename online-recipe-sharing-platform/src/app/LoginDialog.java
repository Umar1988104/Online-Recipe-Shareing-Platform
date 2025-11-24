package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {

    private final UserRepository userRepository;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private User authenticatedUser;

    public LoginDialog(Frame owner, UserRepository userRepository) {
        super(owner, "Login", true);
        this.userRepository = userRepository;
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Online Recipe Sharing Platform");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Please sign in");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::onLogin);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
        });

        JButton forgotButton = new JButton("Forgot password?");
        forgotButton.setBorderPainted(false);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setFocusPainted(false);
        forgotButton.setForeground(Color.GRAY);
        forgotButton.addActionListener(e -> onForgotPassword());

        JButton createAccountButton = new JButton("Create account");
        createAccountButton.setBorderPainted(false);
        createAccountButton.setContentAreaFilled(false);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setForeground(new Color(0, 102, 204));
        createAccountButton.addActionListener(e -> onCreateAccount());

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linksPanel.setOpaque(false);
        linksPanel.add(forgotButton);
        linksPanel.add(createAccountButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        content.add(titleLabel);
        content.add(subtitleLabel);
        content.add(formPanel);
        content.add(Box.createVerticalStrut(5));
        content.add(linksPanel);
        content.add(Box.createVerticalStrut(5));
        content.add(buttonPanel);

        setContentPane(content);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getRootPane().setDefaultButton(loginButton);
    }

    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userRepository.authenticate(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login failed",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            this.authenticatedUser = user;
            dispose();
        }
    }

    public User showDialog() {
        setVisible(true); // blocks until dispose()
        return authenticatedUser;
    }

    private void onForgotPassword() {
        JOptionPane.showMessageDialog(this,
                "This is a demo application.\n" +
                        "Try one of these accounts:\n" +
                        "admin / admin123\n" +
                        "contrib / contrib123\n" +
                        "explorer / explore123",
                "Forgot password",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onCreateAccount() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        String[] roles = {"EXPLORER", "CONTRIBUTOR"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("New username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Create account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userRepository.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                    "That username is already taken.",
                    "Create account",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User.Role role = User.Role.valueOf((String) roleBox.getSelectedItem());
        userRepository.addUser(new User(username, password, role));
        JOptionPane.showMessageDialog(this,
                "Account created. You can now log in.",
                "Create account",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
