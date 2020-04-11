package kernbeisser.StartUp.LogIn;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class DBLogInController implements Controller<DBLogInView,DBLogInModel> {

    private DBLogInView view;
    private DBLogInModel model;

    public DBLogInController(){
        this.model = new DBLogInModel();
        this.view = new DBLogInView(this);
    }

    @Override
    public @NotNull DBLogInView getView() {
        return view;
    }

    @Override
    public @NotNull DBLogInModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
