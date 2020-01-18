package kernbeisser.Windows.LogIn;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.UserMenu.UserMenuController;

public class LogInController implements Controller {
    private LogInView view;
    private LogInModel model;

    LogInController(LogInView view){
        this.view=view;
        this.model=new LogInModel();
        fillABCUser();
        fillAllUser();
    }

    static final int INCORRECT_USERNAME = 0;
    static final int INCORRECT_PASSWORD = 1;
    static final int SUCCESS = 2;

    int logIn() {
        return model.logIn(view.getUsername(),view.getPassword());
    }
    void fillABCUser(){
        for (int i = 97; i < 123; i++) {
            char c  = Character.toUpperCase((char) i);
            view.addTab(Character.toString(c),model.getAllUserWitchBeginsWith(c));
        }
    }
    void fillAllUser(){
        view.addTab("Alle",model.getAllUser());
    }
    void openUserMenu(){
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
