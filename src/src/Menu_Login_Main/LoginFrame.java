package src.Menu_Login_Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        // Set up JFrame properties
        setTitle("WareHouse Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Adjust JFrame size
        setLayout(new BorderLayout());

        // Custom panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("Images/Background_login.jpg"); // Update with the correct path
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Center login form
        backgroundPanel.setPreferredSize(new Dimension(800, 600));

        // Login form panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 255, 255, 180)); // Slightly transparent background
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 30));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(255, 69, 58));
        closeButton.setForeground(Color.WHITE);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals("admin") && password.equals("admin")) {
                    dispose();
                    DashboardFrame dashboard = new DashboardFrame();
                    dashboard.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid Credentials");
                }
            }
        });

        closeButton.addActionListener(e -> System.exit(0));

        // Add components to the login panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(loginLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        loginPanel.add(usernameField, gbc);

        gbc.gridy++;
        loginPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        loginPanel.add(closeButton, gbc);

        // Add login panel to the center of the background panel
        backgroundPanel.add(loginPanel);

        // Add background panel to the frame
        add(backgroundPanel, BorderLayout.CENTER);

        // Center the frame on the screen
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
