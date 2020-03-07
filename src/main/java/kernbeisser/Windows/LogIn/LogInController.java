package kernbeisser.Windows.LogIn;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.UserMenu.UserMenuController;
import kernbeisser.Windows.Window;

public class LogInController implements Controller {
    static final int INCORRECT_USERNAME = 0;
    static final int INCORRECT_PASSWORD = 1;
    static final int SUCCESS = 2;
    private LogInView view;
    private LogInModel model;
    public LogInController(Window current) {
        this.view = new LogInView(current, this);
        this.model = new LogInModel();
        fillABCUser();
        fillAllUser();
    }

    void logIn() {
        int feedback = model.logIn(view.getUsername(), view.getPassword());
        view.applyFeedback(feedback);
        if (feedback == SUCCESS) {
            openUserMenu();
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
        new UserMenuController(view, LogInModel.getLoggedIn());
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
