package kernbeisser.Windows.LogIn.SimpleLogIn;

import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.Verifier.RegexVerifier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
        // TODO the following lines are for testing only! Remove from production code
        File file = new File("testUser.txt");
        if (file.exists()) {
            try {
                List<String> fileLines = Files.readAllLines(file.toPath());
                username.setText(fileLines.get(0));
                password.setText(fileLines.get(1));
            } catch (IOException e) {
                Tools.showUnexpectedErrorWarning(e);
            }
        }
        // TODO test code; remove  up to here
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }


    @Override
    public IconCode getTabIcon() {
        return FontAwesome.SIGN_IN;
    }
}
