import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyLearningJourney extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel, loginPanel, signupPanel, studyPlannerPanel;
    private JTextField loginUsernameField, signupUsernameField, topicField, timespanField, startDateField;
    private JPasswordField loginPasswordField, signupPasswordField;
    private DefaultTableModel tableModel;
    private String loggedInUser;

    // Database details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/learning_journey";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Santhosh@19";

    public MyLearningJourney() {
        setTitle("My Learning Journey");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createMainInterface();
        createSignupInterface();
        createLoginInterface();
        createStudyPlannerInterface();

        add(mainPanel);
        setVisible(true);
    }

    private void createMainInterface() {
        JPanel mainInterface = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("My Learning Journey", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel quoteLabel = new JLabel("\"The beautiful thing about learning is nobody can take it away from you.\"", JLabel.CENTER);
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 18));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");

        loginButton.setFont(new Font("Arial", Font.PLAIN, 20));
        signupButton.setFont(new Font("Arial", Font.PLAIN, 20));

        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        signupButton.addActionListener(e -> cardLayout.show(mainPanel, "Signup"));

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.4;
        mainInterface.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.2;
        mainInterface.add(quoteLabel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.4;
        mainInterface.add(buttonPanel, gbc);

        mainPanel.add(mainInterface, "Main");
    }

    private void createSignupInterface() {
        signupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        signupUsernameField = new JTextField(16);
        signupPasswordField = new JPasswordField(16);
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Arial", Font.PLAIN, 20));

        createAccountButton.addActionListener(e -> createAccount());

        gbc.gridx = 0;
        gbc.gridy = 0;
        signupPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        signupPanel.add(signupUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        signupPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        signupPanel.add(signupPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        signupPanel.add(createAccountButton, gbc);

        mainPanel.add(signupPanel, "Signup");
    }

    private void createLoginInterface() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        loginUsernameField = new JTextField(16);
        loginPasswordField = new JPasswordField(16);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 20));

        loginButton.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        mainPanel.add(loginPanel, "Login");
    }

    private void createStudyPlannerInterface() {
        studyPlannerPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topicField = new JTextField(16);
        timespanField = new JTextField(16);
        startDateField = new JTextField(16);

        JButton createPlanButton = new JButton("Create Plan");
        createPlanButton.setFont(new Font("Arial", Font.PLAIN, 20));
        createPlanButton.addActionListener(e -> addPlan());

        formPanel.add(new JLabel("Topic Name:"));
        formPanel.add(topicField);
        formPanel.add(new JLabel("Timespan (days):"));
        formPanel.add(timespanField);
        formPanel.add(new JLabel("Starting Date (yyyy-mm-dd):"));
        formPanel.add(startDateField);

        studyPlannerPanel.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Topic", "Timespan", "Start Date", "Completion Date"}, 0);
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 3) {
                String completionDate = (String) tableModel.getValueAt(row, col);
                String topic = (String) tableModel.getValueAt(row, 0);
                saveCompletionDate(topic, completionDate);
                checkCompletion(
                        (String) tableModel.getValueAt(row, 1),
                        (String) tableModel.getValueAt(row, 2),
                        completionDate
                );
            }
        });

        studyPlannerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 20));
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            cardLayout.show(mainPanel, "Main");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createPlanButton);
        bottomPanel.add(logoutButton);
        studyPlannerPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(studyPlannerPanel, "StudyPlanner");
    }

    private void createAccount() {
        String username = signupUsernameField.getText();
        String password = new String(signupPasswordField.getPassword());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            cardLayout.show(mainPanel, "Login");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating account.");
        }
    }

    private void login() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUser = username;
                loadPlans();
                cardLayout.show(mainPanel, "StudyPlanner");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error logging in.");
        }
    }

    private void addPlan() {
        String topic = topicField.getText();
        String timespan = timespanField.getText();
        String startDate = startDateField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO plans (username, topic, timespan, start_date) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, loggedInUser);
            stmt.setString(2, topic);
            stmt.setString(3, timespan);
            stmt.setString(4, startDate);
            stmt.executeUpdate();

            tableModel.addRow(new Object[]{topic, timespan, startDate, ""});
            JOptionPane.showMessageDialog(this, "Plan added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding plan.");
        }
    }

    private void loadPlans() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT topic, timespan, start_date, completion_date FROM plans WHERE username = ?")) {
            stmt.setString(1, loggedInUser);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String topic = rs.getString("topic");
                String timespan = rs.getString("timespan");
                String startDate = rs.getString("start_date");
                String completionDate = rs.getString("completion_date");

                tableModel.addRow(new Object[]{topic, timespan, startDate, completionDate != null ? completionDate : ""});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading study plans.");
        }
    }

    private void saveCompletionDate(String topic, String completionDate) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("UPDATE plans SET completion_date = ? WHERE username = ? AND topic = ?")) {
            stmt.setString(1, completionDate);
            stmt.setString(2, loggedInUser);
            stmt.setString(3, topic);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving completion date.");
        }
    }

    private void checkCompletion(String timespan, String startDate, String completionDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long diff = sdf.parse(completionDate).getTime() - sdf.parse(startDate).getTime();
            int daysTaken = (int) (diff / (1000 * 60 * 60 * 24));
            if (daysTaken <= Integer.parseInt(timespan)) {
                JOptionPane.showMessageDialog(this, "Completed within the timespan.");
            } else {
                JOptionPane.showMessageDialog(this, "Not completed within the timespan.");
            }
        } catch (ParseException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyLearningJourney::new);
    }
}
