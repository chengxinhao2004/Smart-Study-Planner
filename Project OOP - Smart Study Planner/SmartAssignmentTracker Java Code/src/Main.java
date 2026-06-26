import view.AppWindow;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        
        // ==================================================
        // GLOBAL UI THEME INJECTION (Dark Tech Dashboard)
        // ==================================================
        try {
            // 1. Define Modern Colors
            Color bgDark = new Color(25, 25, 35);       // Deep space background
            Color panelDark = new Color(35, 35, 50);    // Slightly lighter panels
            Color textLight = new Color(230, 230, 230); // Crisp white text
            Color techAccent = new Color(0, 212, 255);  // Neon Cyan highlight
            
            // 2. Define Larger, Readable Fonts
            Font globalFont = new Font("Segoe UI", Font.PLAIN, 18); // Bumps size from 12 to 18
            Font boldFont = new Font("Segoe UI", Font.BOLD, 18);

            // 3. Force Java to override the default boring UI
            UIManager.put("Panel.background", bgDark);
            UIManager.put("OptionPane.background", bgDark);
            UIManager.put("OptionPane.messageForeground", textLight);
            
            UIManager.put("Label.foreground", textLight);
            UIManager.put("Label.font", globalFont);
            
            UIManager.put("Button.background", panelDark);
            UIManager.put("Button.foreground", techAccent);
            UIManager.put("Button.font", boldFont);
            UIManager.put("Button.focusPainted", false); // Removes ugly click borders
            
            UIManager.put("TextField.background", panelDark);
            UIManager.put("TextField.foreground", textLight);
            UIManager.put("TextField.font", globalFont);
            UIManager.put("TextField.caretForeground", techAccent); // Blinking cursor color
            
            UIManager.put("PasswordField.background", panelDark);
            UIManager.put("PasswordField.foreground", textLight);
            UIManager.put("PasswordField.font", globalFont);
            UIManager.put("PasswordField.caretForeground", techAccent);

            // Makes your list HUGE and readable
            UIManager.put("List.background", panelDark);
            UIManager.put("List.foreground", textLight);
            UIManager.put("List.font", new Font("Segoe UI", Font.PLAIN, 22)); 
            UIManager.put("List.selectionBackground", techAccent);
            UIManager.put("List.selectionForeground", bgDark);
            
         // ------------------------------------------------
            // COMBOBOX (DROPDOWN) FIX
            // ------------------------------------------------
            UIManager.put("ComboBox.background", panelDark);
            UIManager.put("ComboBox.foreground", textLight);
            UIManager.put("ComboBox.font", globalFont);
            
            // This makes the highlighted option blue when you click it
            UIManager.put("ComboBox.selectionBackground", techAccent);
            UIManager.put("ComboBox.selectionForeground", bgDark);
            
            // This targets the exact Gray Arrow Button and turns it Neon Blue!
            UIManager.put("ComboBox.buttonBackground", techAccent); 
            UIManager.put("ComboBox.buttonDarkShadow", techAccent);
            UIManager.put("ComboBox.buttonShadow", techAccent);
            UIManager.put("ComboBox.buttonHighlight", techAccent);
            UIManager.put("CheckBox.background", bgDark);
            UIManager.put("CheckBox.foreground", textLight);
            UIManager.put("CheckBox.font", globalFont);
            
         // --- NEW: Fixes invisible text on Locked Dropdowns ---
            UIManager.put("ComboBox.disabledBackground", panelDark); 
            UIManager.put("ComboBox.disabledForeground", textLight); 
            
        } catch (Exception e) {
            System.out.println("Could not apply custom theme.");
        }

        // ==================================================
        // LAUNCH APPLICATION
        // ==================================================
        javax.swing.SwingUtilities.invokeLater(() -> {
            AppWindow app = new AppWindow();
            app.launch();
        });
        
    }
}