package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SimpleLogInView implements View<SimpleLogInController> {
    private JButton logIn;
    private JPasswordField password;
    private JTextField username;
    private JPanel main;

    private final SimpleLogInController controller;

    SimpleLogInView(SimpleLogInController controller){
        this.controller = controller;
    }

    char[] getPassword(){
        return password.getPassword();
    }

    String getUsername(){
        return username.getText();
    }

    public void accessDenied() {
        JOptionPane.showMessageDialog(getTopComponent(),"Zugriff verweigert. Anmeldedaten sind inkorrect!");
    }

    public void permissionRequired() {
        JOptionPane.showMessageDialog(getTopComponent(),"Zugriff verweigert.\nIhr Benutzerkonto hat Leider nicht die Berechtigung sich anzumelden.\nSie können es bei einem Admin freischalten lassen.");
    }

    @Override
    public void initialize(SimpleLogInController controller) {
        logIn.addActionListener(e -> controller.logIn());
        password.addActionListener(e -> {
            controller.logIn();
        });
        username.addActionListener(e -> password.requestFocus());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
