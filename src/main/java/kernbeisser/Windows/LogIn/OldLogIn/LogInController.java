package kernbeisser.Windows.LogIn.OldLogIn;

import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.LogIn.OldLogIn.LogInView;
import kernbeisser.Windows.UserMenu.UserMenuController;
import kernbeisser.Windows.Window;

public class LogInController implements Controller {
    public static final int INCORRECT_USERNAME = 0;
    public static final int INCORRECT_PASSWORD = 1;
    public static final int SUCCESS = 2;
    private LogInView view;
    private LogInModel model;
    public LogInController(Window current) {
        this.view = new LogInView(current, this);
        this.model = new LogInModel();
        fillABCUser();
        fillAllUser();
    }

    void logIn() {
        try {
            model.logIn(view.getUsername(), view.getPassword());
            openUserMenu();
        } catch (AccessDeniedException e) {
            view.accessDenied();
        }
    }

    private void fillABCUser() {
        for (int i = 97; i < 123; i++) {
            char c = Character.toUpperCase((char) i);
            view.addTab(Character.toString(c), model.getAllUserWitchBeginsWith(c));
        }
    }

    private void fillAllUser() {
        view.addTab("Alle", model.getAllUser());
    }

    private void openUserMenu() {
        new UserMenuController(view);
    }

    @Override
    public void refresh() {

    }

    @Override
    public LogInView getView() {
        return view;
    }

    @Override
    public LogInModel getModel() {
        return model;
    }
}
