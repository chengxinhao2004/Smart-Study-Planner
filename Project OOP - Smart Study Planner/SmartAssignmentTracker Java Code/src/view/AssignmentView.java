package view;

import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AssignmentView {
    
    public void createForm() {
        JFrame frame = new JFrame("Add New Task");
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        // ... (Keep your Course, Task Type, and Urgency Dropdowns from earlier here) ...
        JLabel urgencyLabel = new JLabel("Urgency Level:");
        String[] urgencyLevels = {"Low", "Medium", "High"};
        JComboBox<String> urgencyCombo = new JComboBox<>(urgencyLevels);

        // -------------------------------------------------------------
        // NEW FEATURE: LGoodDatePicker Integration
        // -------------------------------------------------------------
        JLabel dateLabel = new JLabel("Select Due Date:");
        DatePicker datePicker = new DatePicker();
        
        // Optional: Set today's date as the default
        datePicker.setDateToToday(); 

        JButton saveBtn = new JButton("Save Task");

        // Add to frame
        frame.add(urgencyLabel);
        frame.add(urgencyCombo);
        frame.add(dateLabel);
        frame.add(datePicker); // Adds the beautiful calendar widget!
        frame.add(new JLabel("")); // Empty spacer
        frame.add(saveBtn);
        
        frame.setSize(400, 300);
        frame.setVisible(true);
        
        // Action Listener Example
        saveBtn.addActionListener(e -> {
            LocalDate selectedDate = datePicker.getDate();
            System.out.println("User selected deadline: " + selectedDate);
            // Here you would call your Controller to save to the database!
        });
    }
}