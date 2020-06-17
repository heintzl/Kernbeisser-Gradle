package kernbeisser.Windows.Menu;

import kernbeisser.Main;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MenuView implements View<MenuController> {


    private JPanel main;
    private JButton beginCashierSession;
    private JButton printBon;
    private JButton editPriceLists;
    private JButton editArticles;
    private JButton editSurchargeTables;
    private JButton changePassword;
    private JButton valueHistory;
    private JButton editOwnUser;
    private JButton editUserSettings;
    private JButton editUser;
    private JButton transferButton;
    private JButton editPermission;
    private JButton placeHolderButton;
    private JButton changeDataBaseConnection;
    private JButton editApplicationSettings;

    @Override
    public void initialize(MenuController controller) {

    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        Main.buildEnvironment();
        new MenuController().openTab("moin");
    }
}
