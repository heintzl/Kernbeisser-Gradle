package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.UserMenu.UserMenuController;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class SimpleLogInController implements Controller {

    private SimpleLogInView view;
    private LogInModel model;

    public SimpleLogInController(Window current){
        this.model = new LogInModel();
        this.view = new SimpleLogInView(current,this);
    }



    @Override
    public SimpleLogInView getView() {
        return view;
    }


    @Override
    public Model getModel() {
        return model;
    }

    public void logIn() {
        try {
            model.logIn(view.getUsername(),view.getPassword());
            new UserMenuController(view);
        } catch (AccessDeniedException e) {
            view.accessDenied();
        }
    }
}
