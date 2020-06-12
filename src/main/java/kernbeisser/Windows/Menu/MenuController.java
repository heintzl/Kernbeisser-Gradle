package kernbeisser.Windows.Menu;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class MenuController implements Controller<MenuView,MenuModel> {

    private final MenuModel model;
    private final MenuView view;

    public MenuController() {
        model = new MenuModel();
        view = new MenuView();
    }


    @NotNull
    @Override
    public MenuView getView() {
        return view;
    }

    @NotNull
    @Override
    public MenuModel getModel() {
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
