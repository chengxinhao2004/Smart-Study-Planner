package view;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import database.DBConnection; // Make sure this matches your DB connection package!
import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.JButton;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.plaf.basic.BasicArrowButton;
import java.time.LocalDate;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

public class AppWindow {
    
    private JFrame frame;
    private JPanel cardPanel; 
    private CardLayout cardLayout; 

    private JLabel dashboardWelcomeLabel; 
    private int currentUserId = 1; 
    private JPanel planListContainer = new JPanel(); 
    private JPanel recycleBinContainer = new JPanel(); 
    private String defaultSavePath = null;
    
    public void launch() {
        frame = new JFrame("UTeM Smart Study Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Made slightly larger to fit the charts

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 1. Initialize all your requested screens
        JPanel loginScreen = buildLoginScreen();
        JPanel registerScreen = buildRegisterScreen();
        JPanel dashboardScreen = buildDashboardScreen();
        JPanel setupPlanScreen = buildSetupPlanScreen(); // Adds the new screen
        JPanel checkPlanScreen = buildCheckPlanScreen();
        JPanel recycleBinScreen = buildRecycleBinScreen();
        JPanel achievementScreen = buildAchievementScreen();
        // (You will build the SetUpScreen later)

        // 2. Add them to the "Deck" with a specific String name
        cardPanel.add(loginScreen, "LOGIN");
        cardPanel.add(registerScreen, "REGISTER");
        cardPanel.add(dashboardScreen, "DASHBOARD");
        cardPanel.add(setupPlanScreen, "SETUP_PLAN");
        cardPanel.add(checkPlanScreen, "CHECK_PLAN");
        cardPanel.add(recycleBinScreen, "RECYCLE_BIN"); 
        cardPanel.add(achievementScreen, "ACHIEVEMENT");

        frame.add(cardPanel);
        frame.setVisible(true);
    }

 // ==========================================
    // SCREEN 1: LOGIN (Responsive Fix)
    // ==========================================
    private JPanel buildLoginScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // FIX 1: Reduced the side borders from 150 to 80 so it fits on smaller screens
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80)); 
        
        JPanel wrapperPanel = new JPanel(new BorderLayout(0, 30)); 

        JLabel title = new JLabel("UTeM Student Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 20)); 
        
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();
        
        JCheckBox showPassBox = new JCheckBox("Show Password");
        showPassBox.addActionListener(e -> {
            if (showPassBox.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('\u2022'); 
            }
        });
        
        // FIX 2: Switched to GridLayout(1 Row, 2 Columns) so they NEVER wrap or hide!
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0)); 
        JButton registerBtn = new JButton("Register NOW");
        JButton loginBtn = new JButton("Login");

        registerBtn.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER"));
        
        loginBtn.addActionListener(e -> {
            String matrixNumber = idField.getText();
            String password = new String(passField.getPassword());
            
            if (matrixNumber.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both your Matrix Number and Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            
            String sql = "SELECT * FROM users WHERE matrix_number = ? AND password = ?";
            try (Connection conn = DBConnection.getConnection();
            	     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, matrixNumber);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String fullName = rs.getString("name");
                        currentUserId = rs.getInt("id"); 
                        String firstName = fullName.split(" ")[0]; 
                        
                        dashboardWelcomeLabel.setText("Welcome to your Dashboard, " + firstName + "!");
                        JOptionPane.showMessageDialog(frame, "Login Successful! Welcome, " + firstName);
                        
                        idField.setText("");
                        passField.setText("");
                        showPassBox.setSelected(false); 
                        passField.setEchoChar('\u2022'); 
                        
                        cardLayout.show(cardPanel, "DASHBOARD");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid Matrix Number or Password.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnPanel.add(registerBtn); 
        btnPanel.add(loginBtn);    

        formPanel.add(new JLabel("Matrix Number:"));
        formPanel.add(idField);
        
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);
        
        formPanel.add(new JLabel("")); 
        formPanel.add(showPassBox);
        
        formPanel.add(new JLabel("")); 
        formPanel.add(btnPanel); 
        
        wrapperPanel.add(title, BorderLayout.NORTH);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(wrapperPanel, BorderLayout.NORTH); 
        
        return mainPanel;
    }

    
 // ==========================================
    // AUTO-ID GENERATOR (Gap-Finding Loop)
    // ==========================================
    private String generateNextMatrixNumber() {
        int expectedId = 1; // We always start looking for 0001
        
        // Order is crucial here! We MUST sort them from lowest to highest
        String sql = "SELECT matrix_number FROM users ORDER BY matrix_number ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                // Convert the database string (e.g., "0001") into a real integer (1)
                int currentId = Integer.parseInt(rs.getString("matrix_number"));
                
                if (currentId == expectedId) {
                    // The number is taken! Increase our expectation and check the next row.
                    expectedId++; 
                } else if (currentId > expectedId) {
                    // We found a gap! The expectedId is completely missing from the database.
                    break; 
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error calculating next ID.");
            e.printStackTrace();
        }
        
        // Convert our found integer back into the "000X" string format
        return String.format("%04d", expectedId);
    }

 // ==========================================
    // FILE SAVE LOCATION
    // ==========================================
    private void saveProReport(String title, String details, String date) {
        // --- 1. Calculate Time Remaining ---
        LocalDateTime deadline = LocalDateTime.parse(date + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();
        long daysLeft = ChronoUnit.DAYS.between(now, deadline);
        long hoursLeft = ChronoUnit.HOURS.between(now, deadline) % 24;

        // --- 2. Determine Rank ---
        // Fetch tasks to find the rank of THIS current work
        String rankSql = "SELECT title FROM plans WHERE user_id = ? AND is_active = 1 ORDER BY due_date ASC LIMIT 4";
        int rank = -1;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(rankSql)) {
            pstmt.setInt(1, currentUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 1;
                while(rs.next()) {
                    if(rs.getString("title").equals(title)) { rank = count; break; }
                    count++;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // --- 3. Generate Pro Report Content ---
        String report = 
            "==========================================================\n" +
            "                UTeM SMART STUDY PLANNER                 \n" +
            "                 OFFICIAL PROGRESS REPORT                \n" +
            "==========================================================\n\n" +
            "   WORK TITLE : " + title.toUpperCase() + "\n" +
            "   DUE DATE   : " + date + "\n\n" +
            "----------------------------------------------------------\n" +
            "   TIME REMAINING: " + daysLeft + " Days, " + hoursLeft + " Hours \n" +
            "   URGENCY RANK  : " + (rank != -1 ? "TOP " + rank + " PRIORITY" : "N/A") + "\n" +
            "----------------------------------------------------------\n\n" +
            "   STUDY NOTES / DETAILS: \n" +
            "   " + details + "\n\n" +
            "==========================================================\n" +
            "   Keep pushing forward! Your future self will thank you.  \n" +
            "==========================================================";

        // --- 4. Save the file ---
        if (defaultSavePath == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                defaultSavePath = fileChooser.getSelectedFile().getAbsolutePath();
            } else return;
        }
        
        File file = new File(defaultSavePath + File.separator + title + "_Pro_Report.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(report);
            JOptionPane.showMessageDialog(frame, "Pro Report Generated Successfully!\nRank: " + rank);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
 // // ==========================================
    // SCREEN 2: REGISTRATION (Fully Corrected)
    // ==========================================
    private JPanel buildRegisterScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); 

        JLabel title = new JLabel("Register New Student", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22)); 
        
        // 5 rows, 2 columns for the input fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        
        JTextField idField = new JTextField(generateNextMatrixNumber());
        idField.setEditable(false); 
        
        JTextField nameField = new JTextField();
        JPasswordField passField1 = new JPasswordField();
        JPasswordField passField2 = new JPasswordField(); 
        
        JLabel passInstruction = new JLabel("Password (Min 5 chars + 1 symbol):");
        passInstruction.setForeground(Color.RED); 
        
        JCheckBox showPassBox = new JCheckBox("Show Passwords");
        showPassBox.addActionListener(e -> {
            if (showPassBox.isSelected()) {
                passField1.setEchoChar((char) 0);
                passField2.setEchoChar((char) 0);
            } else {
                passField1.setEchoChar('\u2022');
                passField2.setEchoChar('\u2022');
            }
        });
        
        // --- BUTTONS MOVED TO FOOTER ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton registerBtn = new JButton("Confirm Registration");
        JButton backBtn = new JButton("Back to Login");

        registerBtn.setPreferredSize(new Dimension(220, 40));
        backBtn.setPreferredSize(new Dimension(180, 40));
        
        footerPanel.add(registerBtn);
        footerPanel.add(backBtn);
        
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String pass1 = new String(passField1.getPassword());
            String pass2 = new String(passField2.getPassword());
            
            if (name.isEmpty() || pass1.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields!");
                return;
            }
            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match. Try again.");
                return;
            }
            if (pass1.length() < 5 || !pass1.matches(".*[!@#$%^&*()_+=-].*")) {
                JOptionPane.showMessageDialog(frame, "Password must be at least 5 characters and contain a symbol!");
                return;
            }
            
            String sql = "INSERT INTO users (matrix_number, name, password) VALUES (?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, idField.getText());
                pstmt.setString(2, name);
                pstmt.setString(3, pass1); 
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(frame, "Registration Success! Your ID is: " + idField.getText());
                
                
                // Reset form
                idField.setText(generateNextMatrixNumber());
                nameField.setText("");
                passField1.setText("");
                passField2.setText("");
                showPassBox.setSelected(false);
                passField1.setEchoChar('\u2022');
                passField2.setEchoChar('\u2022');
                
                cardLayout.show(cardPanel, "LOGIN");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Assemble Form
        formPanel.add(new JLabel("Your Auto-Generated Matrix Number:"));
        formPanel.add(idField);
        
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        
        formPanel.add(passInstruction);
        formPanel.add(passField1);
        
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(passField2);
        
        formPanel.add(new JLabel("")); 
        formPanel.add(showPassBox);
        
        // Assemble Main Panel
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH); // Buttons are here now!
        
        return mainPanel;
    }
 
 // ==========================================
    // SCREEN 3: THE DASHBOARD (With Logout & Exit)
    // ==========================================
    private JPanel buildDashboardScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100)); 
        
        JPanel wrapperPanel = new JPanel(new BorderLayout(0, 40));
        
        dashboardWelcomeLabel = new JLabel("Welcome to your Dashboard!", SwingConstants.CENTER);
        dashboardWelcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        // Changed to 4 rows to fit the new Logout button
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 15, 20));
        
        JButton newPlanBtn = new JButton("Set Up New Plan");
        JButton checkPlanBtn = new JButton("Check My Latest Plan");
        JButton achievementBtn = new JButton("View My Achievement");
        
        // --- NEW: The Log Out and Exit Button ---
        JButton logoutBtn = new JButton("Log Out and Exit");
        logoutBtn.setBackground(new Color(255, 100, 100)); // Make it slightly red for danger
        logoutBtn.setForeground(Color.WHITE);

        // --- The Button Routing ---
        newPlanBtn.addActionListener(e -> cardLayout.show(cardPanel, "SETUP_PLAN")); // THIS FIXES YOUR ERROR!
        checkPlanBtn.addActionListener(e -> {
            // This line forces the database to refresh the list right before you look at it!
            loadPlansFromDatabase(""); 
            cardLayout.show(cardPanel, "CHECK_PLAN");
        });
     // --- UPGRADED ACHIEVEMENT BUTTON ROUTING ---
        achievementBtn.addActionListener(e -> {
            // 1. Completely discard the old Achievement screen
            // 2. Build a brand new one that pulls fresh data for currentUserId
            JPanel freshAchievementScreen = buildAchievementScreen();
            
            // 3. Add this fresh screen to the cardPanel
            cardPanel.add(freshAchievementScreen, "ACHIEVEMENT");
            
            // 4. Show the fresh screen
            cardLayout.show(cardPanel, "ACHIEVEMENT");
        });
        
        // --- The Exit Logic with Yes/No ---
     // --- The Improved Secure Logout Logic ---
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to log out?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                currentUserId = -1;       // Securely reset the user ID
                defaultSavePath = null;   // Wipes the folder memory for the next user
                cardLayout.show(cardPanel, "LOGIN"); // Return to login screen
            }
        });

        // Make the buttons proportional
        newPlanBtn.setPreferredSize(new Dimension(200, 40));
        checkPlanBtn.setPreferredSize(new Dimension(200, 40));
        achievementBtn.setPreferredSize(new Dimension(200, 40));
        logoutBtn.setPreferredSize(new Dimension(200, 40));

        menuPanel.add(newPlanBtn);
        menuPanel.add(checkPlanBtn);
        menuPanel.add(achievementBtn);
        menuPanel.add(logoutBtn); // Added to the bottom of the list
        
        wrapperPanel.add(dashboardWelcomeLabel, BorderLayout.NORTH);
        wrapperPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(wrapperPanel, BorderLayout.NORTH);
        
        return mainPanel;
    }

 // ==========================================
    // SCREEN 4: SET UP NEW PLAN (Perfected UI Colors)
    // ==========================================
    private JPanel buildSetupPlanScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); 

        JLabel title = new JLabel("Set Up Your Study Plan", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        
     // --- NEW: Fetch Categories from Database ---
        JComboBox<String> categoryCombo = new JComboBox<>();
        try (Connection conn = DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM categories")) {
            
            boolean hasCategories = false;
            while(rs.next()) {
                categoryCombo.addItem(rs.getString("name"));
                hasCategories = true;
            }
            
            // Fallback just in case the database table is empty
            if (!hasCategories) {
                categoryCombo.addItem("Homework");
                categoryCombo.addItem("Assignment");
            }
        } catch (Exception e) { 
            System.out.println("Error loading categories from DB.");
            categoryCombo.addItem("Homework"); // Fallback if DB fails
            e.printStackTrace(); 
        }
        
        // --- NEW: Fixes the Dropdown Arrow (Dark background, Light Blue Triangle) ---
        categoryCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                // Parameters: Direction, Background, Shadow, DarkShadow (The Arrow Color!), Highlight
                return new BasicArrowButton(
                    BasicArrowButton.SOUTH,
                    new Color(35, 35, 50),  
                    new Color(35, 35, 50),  
                    new Color(0, 212, 255), 
                    new Color(35, 35, 50)   
                );
            }
        });
        
        JTextField titleField = new JTextField();
        JTextField detailsField = new JTextField(); 
        
        // --- NEW: Fixes the Calendar (Light Blue Text and Arrows) ---
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, new Color(0, 212, 255));
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons, new Color(0, 212, 255));
        
        DatePicker datePicker = new DatePicker(dateSettings); 
        datePicker.setDateToToday(); 
        
        String[] durations = {"Daily", "Weekly", "Monthly", "None"};
        JComboBox<String> durationCombo = new JComboBox<>(durations);
        
        // --- NEW: Fixes the Duration Dropdown Arrow ---
        durationCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new BasicArrowButton(
                    BasicArrowButton.SOUTH,
                    new Color(35, 35, 50),  
                    new Color(35, 35, 50),  
                    new Color(0, 212, 255), 
                    new Color(35, 35, 50)   
                );
            }
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton saveBtn = new JButton("Save Plan");
        JButton backBtn = new JButton("Back to Dashboard");

        btnPanel.add(saveBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(backBtn);
        
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "DASHBOARD"));
        
        saveBtn.addActionListener(e -> {
            String cat = (String) categoryCombo.getSelectedItem();
            String planTitle = titleField.getText();
            String details = detailsField.getText();
            String duration = (String) durationCombo.getSelectedItem();
            
            String pickedDate = datePicker.getDateStringOrEmptyString();
            
            if (planTitle.isEmpty() || pickedDate.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in the title and select a date!");
                return;
            }
            
            String finalDateTime = pickedDate + " 23:59:59";
            
            String sql = "INSERT INTO plans (user_id, plan_category, title, details, due_date, reminder_duration) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, currentUserId); 
                pstmt.setString(2, cat);
                pstmt.setString(3, planTitle);
                pstmt.setString(4, details);
                pstmt.setString(5, finalDateTime); 
                pstmt.setString(6, duration);
                
                pstmt.executeUpdate();
                
                if (duration.equals("None")) {
                    JOptionPane.showMessageDialog(frame, 
                        "Plan Saved!\n\nDon't forget to try this remind feature, it can help remind your work better. " +
                        "You can set the remind again anytime in Check My Latest Plan.", 
                        "Feature Tip", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Object[] customOptions = {"Yes, let's try", "No, later"};
                    int reportConfirm = JOptionPane.showOptionDialog(frame, 
                        "Plan Saved! Would you like to try receive the first Reminder Report Now?", 
                        "Download Report", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, 
                        customOptions, 
                        customOptions[0]); 
                        
                    if (reportConfirm == 0) { 
                        // Call the method instead of showing a fake message
                        saveProReport(planTitle, details, pickedDate); 
                    } else {
                        JOptionPane.showMessageDialog(frame, "Got it. Reminders will follow your " + duration + " schedule.");
                    }
                }
                
                titleField.setText("");
                detailsField.setText("");
                datePicker.setDateToToday();
                cardLayout.show(cardPanel, "DASHBOARD");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Database error. Please try again.");
                ex.printStackTrace();
            }
        });

        formPanel.add(new JLabel("Work Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Title / Name of Work:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Work Details:"));
        formPanel.add(detailsField);
        formPanel.add(new JLabel("Deadline Date:"));
        formPanel.add(datePicker); 
        formPanel.add(new JLabel("Reminder Report Duration:"));
        formPanel.add(durationCombo);
        formPanel.add(new JLabel("")); 
        formPanel.add(btnPanel);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
 
 
 
 // ==========================================
    // SCREEN 5: CHECK LATEST PLAN (Custom Row Layout)
    // ==========================================
    private JPanel buildCheckPlanScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); 
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField searchField = new JTextField(25);
        JButton searchBtn = new JButton("Search");
        searchPanel.add(new JLabel("Search Work Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // Setup the container for our custom rows
        planListContainer.setLayout(new BoxLayout(planListContainer, BoxLayout.Y_AXIS));
        planListContainer.setBackground(new Color(25, 25, 35)); // Match the dark theme
        
        JScrollPane scrollPane = new JScrollPane(planListContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        loadPlansFromDatabase(""); // Initial load

        searchBtn.addActionListener(e -> {
            loadPlansFromDatabase(searchField.getText());
        });

     // --- UPGRADED ACTION PANEL (Added Recycle Bin Button) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton backBtn = new JButton("Back to Dashboard");
        JButton recycleBtn = new JButton("Recycle Bin");
        
        // Give the recycle button a slightly different color so it stands out
        recycleBtn.setForeground(new Color(255, 100, 100)); 

        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "DASHBOARD"));
        recycleBtn.addActionListener(e -> {
            loadDeletedPlansFromDatabase(""); // Load the trash data
            cardLayout.show(cardPanel, "RECYCLE_BIN"); // Flip to the new screen
        });

        actionPanel.add(backBtn);
        actionPanel.add(recycleBtn);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==========================================
    // CHECK LATEST PLAN ROW GENERATOR
    // ==========================================
    private void loadPlansFromDatabase(String keyword) {
        planListContainer.removeAll();
        
        // Ensure you are using the WHERE user_id = ? filter to stop mixing data!
        String sql = "SELECT title FROM plans WHERE user_id = ? AND title LIKE ? AND is_active = 1";
        
        // Structure it like this:
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, currentUserId); // Lock to current user
            pstmt.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    String taskTitle = rs.getString("title");
                    
                    // --- Build Individual Row Panel ---
                    JPanel row = new JPanel(new BorderLayout());
                    row.setBackground(new Color(35, 35, 50)); 
                    row.setBorder(new EmptyBorder(10, 15, 10, 15));
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Stop vertical stretching
                    
                    JLabel titleLabel = new JLabel(taskTitle);
                    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    
                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                    btnPanel.setBackground(new Color(35, 35, 50));
                    
                    JButton viewBtn = new JButton("VIEW");
                    JButton deleteBtn = new JButton("DELETE");
                    
                    // Route to the new Pro Overview page
                    viewBtn.addActionListener(e -> openDetailedViewPage(taskTitle));
                    
                    deleteBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(frame, 
                            "Are you sure you want to delete '" + taskTitle + "'?", 
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try (Connection delConn = DBConnection.getConnection();
                                 PreparedStatement delStmt = delConn.prepareStatement("UPDATE plans SET is_active = 0 WHERE title = ?")) {
                                delStmt.setString(1, taskTitle);
                                delStmt.executeUpdate();
                                loadPlansFromDatabase(""); // Refresh the list!
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                    });

                    btnPanel.add(viewBtn);
                    btnPanel.add(deleteBtn);
                    
                    row.add(titleLabel, BorderLayout.CENTER);
                    row.add(btnPanel, BorderLayout.EAST); // Pushes buttons to the right
                    
                    planListContainer.add(row);
                    planListContainer.add(Box.createVerticalStrut(10)); // Gap between rows
                }
                
                if (!hasData) {
                    JLabel emptyLabel = new JLabel("No active plans found. Go set up a new plan!");
                    emptyLabel.setForeground(Color.GRAY);
                    planListContainer.add(emptyLabel);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        
        planListContainer.revalidate(); // Force Java to redraw the UI
        planListContainer.repaint();
    }
    
 // ==========================================
    // SCREEN 6: RECYCLE BIN
    // ==========================================
    private JPanel buildRecycleBinScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); 

        JLabel titleLabel = new JLabel("Recycle Bin (Deleted Plans)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        recycleBinContainer.setLayout(new BoxLayout(recycleBinContainer, BoxLayout.Y_AXIS));
        recycleBinContainer.setBackground(new Color(25, 25, 35)); 
        
        JScrollPane scrollPane = new JScrollPane(recycleBinContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backBtn = new JButton("Back to Active Plans");

        backBtn.addActionListener(e -> {
            loadPlansFromDatabase(""); // Refresh the active list just in case they recovered something
            cardLayout.show(cardPanel, "CHECK_PLAN");
        });
        
        actionPanel.add(backBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==========================================
    // RECYCLE BIN ROW GENERATOR
    // ==========================================
    private void loadDeletedPlansFromDatabase(String keyword) {
        recycleBinContainer.removeAll(); 
        
        // ONLY fetch rows where is_active = 0
        String sql = "SELECT * FROM plans WHERE user_id = ? AND title LIKE ? AND is_active = 0";
        
        try (Connection conn = DBConnection.getConnection();
        	     PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	     
        	    pstmt.setInt(1, currentUserId); // Lock to the current user!
        	    pstmt.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    String taskTitle = rs.getString("title");
                    
                    JPanel row = new JPanel(new BorderLayout());
                    row.setBackground(new Color(35, 35, 50)); 
                    row.setBorder(new EmptyBorder(10, 15, 10, 15));
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); 
                    
                    JLabel titleLabel = new JLabel(taskTitle);
                    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    // Make the text greyed out so it looks "deleted"
                    titleLabel.setForeground(Color.GRAY); 
                    
                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                    btnPanel.setBackground(new Color(35, 35, 50));
                    
                    JButton recoverBtn = new JButton("RECOVER");
                    JButton removeBtn = new JButton("REMOVE");
                    removeBtn.setForeground(new Color(255, 100, 100)); // Danger Red
                    
                    // --- OPTION 1: RECOVER ---
                    recoverBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(frame, 
                            "Recover '" + taskTitle + "' back to your active plans?", 
                            "Confirm Recovery", JOptionPane.YES_NO_OPTION);
                            
                        if (confirm == JOptionPane.YES_OPTION) {
                            try (Connection recConn = DBConnection.getConnection();
                                 PreparedStatement recStmt = recConn.prepareStatement("UPDATE plans SET is_active = 1 WHERE title = ?")) {
                                recStmt.setString(1, taskTitle);
                                recStmt.executeUpdate();
                                
                                DBConnection.logAction(currentUserId, "Recovered plan: " + taskTitle);
                                
                                loadDeletedPlansFromDatabase(""); // Refresh the trash list
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                    });
                    
                    // --- OPTION 2: PERMANENT REMOVE ---
                    removeBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(frame, 
                            "Are you sure you want to permanently remove '" + taskTitle + "'?\nIt will be lost forever.", 
                            "Permanent Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            
                        if (confirm == JOptionPane.YES_OPTION) {
                            try (Connection delConn = DBConnection.getConnection();
                                 PreparedStatement delStmt = delConn.prepareStatement("DELETE FROM plans WHERE title = ?")) {
                                delStmt.setString(1, taskTitle);
                                delStmt.executeUpdate();
                                
                                DBConnection.logAction(currentUserId, "Deleted plan: " + taskTitle);
                                
                                loadDeletedPlansFromDatabase(""); // Refresh the trash list
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                    });

                    btnPanel.add(recoverBtn);
                    btnPanel.add(removeBtn);
                    
                    row.add(titleLabel, BorderLayout.CENTER);
                    row.add(btnPanel, BorderLayout.EAST); 
                    
                    recycleBinContainer.add(row);
                    recycleBinContainer.add(Box.createVerticalStrut(10)); 
                }
                
                if (!hasData) {
                    JLabel emptyLabel = new JLabel("Recycle Bin is empty. You're all clean!");
                    emptyLabel.setForeground(Color.GRAY);
                    recycleBinContainer.add(emptyLabel);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        
        recycleBinContainer.revalidate(); 
        recycleBinContainer.repaint();
    }

    
    
 // ==========================================
    // SCREEN 7: PRO OVERVIEW & EDIT PAGE
    // ==========================================
    private void openDetailedViewPage(String originalTitle) {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); 

        JLabel headerTitle = new JLabel("Plan Overview: " + originalTitle, SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        
        String[] categories = {"Homework", "Assignment", "GroupWork", "Final Project", "Test and Revision"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setEnabled(false); // <--- THIS LOCKS THE CATEGORY SO IT CANNOT BE CHANGED
        
        JTextField titleField = new JTextField();
        JTextField detailsField = new JTextField(); 
        
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, new Color(0, 212, 255));
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons, new Color(0, 212, 255));
        DatePicker datePicker = new DatePicker(dateSettings); 
        
        String[] durations = {"Daily", "Weekly", "Monthly", "None"};
        JComboBox<String> durationCombo = new JComboBox<>(durations);
        
     // --- NEW: Apply the custom UI theme to BOTH dropdowns! ---
        categoryCombo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                return new javax.swing.plaf.basic.BasicArrowButton(javax.swing.plaf.basic.BasicArrowButton.SOUTH,
                    new Color(35, 35, 50), new Color(35, 35, 50), new Color(0, 212, 255), new Color(35, 35, 50));
            }
        });

        durationCombo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                return new javax.swing.plaf.basic.BasicArrowButton(javax.swing.plaf.basic.BasicArrowButton.SOUTH,
                    new Color(35, 35, 50), new Color(35, 35, 50), new Color(0, 212, 255), new Color(35, 35, 50));
            }
        });

        // --- FETCH EXISTING DATA FROM XAMPP ---
        String fetchSql = "SELECT * FROM plans WHERE title = ? AND is_active = 1 LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(fetchSql)) {
            
            pstmt.setString(1, originalTitle);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    categoryCombo.setSelectedItem(rs.getString("plan_category"));
                    titleField.setText(rs.getString("title"));
                    detailsField.setText(rs.getString("details"));
                    durationCombo.setSelectedItem(rs.getString("reminder_duration"));
                    
                    // Safely parse the SQL Date back into the Calendar widget
                    String dbDate = rs.getString("due_date"); 
                    if (dbDate != null && dbDate.contains(" ")) {
                        String justDate = dbDate.split(" ")[0]; // Cuts off the time part
                        datePicker.setDate(LocalDate.parse(justDate));
                    }
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton saveBtn = new JButton("Update Details");
        JButton downloadBtn = new JButton("Download Report");
        JButton backBtn = new JButton("Back to List");

        btnPanel.add(saveBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(downloadBtn);
        btnPanel.add(backBtn);
        
        downloadBtn.addActionListener(e -> {
            saveProReport(originalTitle, detailsField.getText(), datePicker.getDateStringOrEmptyString());
        });
        
        backBtn.addActionListener(e -> {
            loadPlansFromDatabase(""); // Refresh the list in case they deleted something
            cardLayout.show(cardPanel, "CHECK_PLAN");
        });
        
        saveBtn.addActionListener(e -> {
            String newTitle = titleField.getText();
            String newDetails = detailsField.getText();
            String newDuration = (String) durationCombo.getSelectedItem();
            String pickedDate = datePicker.getDateStringOrEmptyString();
            
            if (newTitle.isEmpty() || pickedDate.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Title and Date cannot be empty!");
                return;
            }
            
            String finalDateTime = pickedDate + " 23:59:59";
            
            // --- UPDATE THE DATABASE ---
            String updateSql = "UPDATE plans SET title = ?, details = ?, due_date = ?, reminder_duration = ? WHERE title = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                
                pstmt.setString(1, newTitle);
                pstmt.setString(2, newDetails);
                pstmt.setString(3, finalDateTime);
                pstmt.setString(4, newDuration);
                pstmt.setString(5, originalTitle); // The WHERE clause target
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Plan Details Updated Successfully!");
                
                // Go back to the list automatically
                loadPlansFromDatabase("");
                cardLayout.show(cardPanel, "CHECK_PLAN");
                
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        formPanel.add(new JLabel("Work Category (Locked):"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Title / Name of Work:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Work Details:"));
        formPanel.add(detailsField);
        formPanel.add(new JLabel("Deadline Date:"));
        formPanel.add(datePicker); 
        formPanel.add(new JLabel("Reminder Report Duration:"));
        formPanel.add(durationCombo);
        formPanel.add(new JLabel("")); 
        formPanel.add(btnPanel);

        mainPanel.add(headerTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // --- INJECT THE PAGE INTO THE DECK ---
        cardPanel.add(mainPanel, "VIEW_PLAN_DYNAMIC");
        cardLayout.show(cardPanel, "VIEW_PLAN_DYNAMIC");
    }
    
    
 // ==========================================================
    // SCREEN 8: VIEW ACHIEVEMENT (Full Width Layout)
    // ==========================================================
    private JPanel buildAchievementScreen() {
        // Use a standard BorderLayout without the extra wrapper
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String sql = "SELECT title, due_date FROM plans WHERE user_id = ? AND is_active = 1 ORDER BY due_date ASC LIMIT 4";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId); 
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    dataset.addValue(calculateUrgencyScore(LocalDateTime.parse(rs.getString("due_date"), formatter)), 
                                    "Score", rs.getString("title"));
                }
                if (!hasData) dataset.addValue(0, "Score", "No Tasks");
            }
        } catch (Exception e) { e.printStackTrace(); }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Urgency Tracker", null, "Score (0-100)", dataset, 
                PlotOrientation.VERTICAL, false, true, false);

        // --- DESIGN CONFIGURATION ---
        barChart.setAntiAlias(true);
        barChart.setTextAntiAlias(true);
        
        org.jfree.chart.plot.CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setRange(0, 100);
        
        // Balanced margins - not too thin, not too wide
        plot.getDomainAxis().setCategoryMargin(0.3); 
       
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        // Remove setPreferredSize so it takes available space in BorderLayout.CENTER
        
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setPreferredSize(new Dimension(0, 45)); // Keeps button a fixed height
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "DASHBOARD"));

        // Adding directly to panel to fill space
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==========================================
    // THE CALCULATION MODULE
    // ==========================================
    private double calculateUrgencyScore(LocalDateTime dueDate) {
        LocalDateTime now = LocalDateTime.now();
        long daysRemaining = ChronoUnit.DAYS.between(now, dueDate);

        double baseScore = 50.0; // Starting baseline
        double finalScore = 0.0;

        if (daysRemaining <= 0) {
            finalScore = 100.0; // Maximum panic, due today or overdue!
        } else if (daysRemaining <= 3) {
            finalScore = baseScore + 40.0; // 90/100
        } else if (daysRemaining <= 7) {
            finalScore = baseScore + 25.0; // 75/100
        } else if (daysRemaining <= 14) {
            finalScore = baseScore + 10.0; // 60/100
        } else {
            finalScore = 20.0; // Plenty of time, low urgency
        }

        return finalScore;
    }
}

