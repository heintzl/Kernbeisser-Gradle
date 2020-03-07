package kernbeisser.StartUp.LogIn;

import kernbeisser.Config.ConfigManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Windows.Window;
import org.json.JSONObject;

import javax.swing.*;

public class DBLogIn extends Window {
    private JButton logIn;
    private JTextField url;
    private JTextField username;
    private JPasswordField password;
    private JButton cancel;
    private JPanel main;

    public DBLogIn(Window current) {
        super(current);
        add(main);
        JSONObject access = ConfigManager.getDBAccess();
        url.setText(access.getString("URL"));
        username.setText(access.getString("Username"));
        logIn.addActionListener(e -> {
            String newUrl = url.getText();
            String newUsername = username.getText();
            String newPassword = new String(password.getPassword());
            if (DBConnection.tryLogIn(newUrl, newUsername, newPassword)) {
                access.put("URL", newUrl);
                access.put("Username", newUsername);
                access.put("Password", newPassword);
                ConfigManager.updateFile();
                JOptionPane.showMessageDialog(this, "Die Verbindung wurde erfolgreich erstellt!");
                back();
            } else {
                JOptionPane.showMessageDialog(this,
                                              "Es kann leider keine Verbindung hergestellt werden,\n bitte \u00fcberpr\u00fcfen sie die Eingaben nach Fehlern");
            }
        });
        cancel.addActionListener(e -> {
            back();
        });
    }

}
