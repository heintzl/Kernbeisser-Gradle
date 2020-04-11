package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Enums.Key;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.UserMenu.UserMenuController;
import org.jetbrains.annotations.NotNull;

public class SimpleLogInController implements Controller<SimpleLogInView,SimpleLogInModel> {

    private SimpleLogInView view;
    private SimpleLogInModel model;

    public SimpleLogInController(){
        this.model = new SimpleLogInModel();
        this.view = new SimpleLogInView(this);
    }



    @Override
    public @NotNull SimpleLogInView getView() {
        return view;
    }


    @Override
    public @NotNull SimpleLogInModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void logIn() {
        try {
            model.logIn(view.getUsername(),view.getPassword());
            new UserMenuController().openAsWindow(view.getWindow(), JFrameWindow::new);
        } catch (AccessDeniedException e) {
            view.accessDenied();
        } catch (PermissionRequired permissionRequired) {
            view.permissionRequired();
        }
    }
}
