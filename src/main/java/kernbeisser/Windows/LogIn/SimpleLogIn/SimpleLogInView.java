package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class SimpleLogInView extends Window implements View {
    private JButton logIn;
    private JPasswordField password;
    private JTextField username;
    private JPanel main;


    SimpleLogInView(Window current,SimpleLogInController controller){
        super(current);
        add(main);
        logIn.addActionListener(e -> controller.logIn());
        password.addActionListener(e -> controller.logIn());
        username.addActionListener(e -> password.requestFocus());
        windowInitialized();
    }

    char[] getPassword(){
        return password.getPassword();
    }

    String getUsername(){
        return username.getText();
    }

    public void accessDenied() {
        JOptionPane.showMessageDialog(this,"Zugriff verweigert. Anmeldedaten sind inkorrect!");
    }

    public void permissionRequired() {
        JOptionPane.showMessageDialog(this,"Zugriff verweigert.\nIhr Benutzerkonto hat Leider nicht die Berechtigung sich anzumelden.\nSie können es bei einem Admin freischalten lassen.");
    }

}
