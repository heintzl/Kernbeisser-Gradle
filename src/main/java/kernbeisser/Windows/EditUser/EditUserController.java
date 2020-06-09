package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Selector.SelectorController;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class EditUserController implements Controller<EditUserView,EditUserModel> {
    private EditUserView view;
    private final EditUserModel model;

    public EditUserController(User user, Mode mode) {
        model = new EditUserModel(user == null ? new User() : user, mode);
        if (mode == Mode.REMOVE) {
            model.doAction(user);
        } else {
            this.view = new EditUserView(this);
        }
        if(mode==Mode.EDIT)
            view.setUniqueVerifier(user);
        if(mode==Mode.ADD)
            view.setUniqueVerifier();
    }

    private void changePassword(String to) {
        model.getUser().setPassword(BCrypt.withDefaults().hashToString(Setting.HASH_COSTS.getIntValue(), to.toCharArray()));
    }

    void requestChangePassword() {
        String password = view.requestPassword();
        if(password==null)return;
        if (password.length() < 4) {
            view.passwordToShort();
            requestChangePassword();
        } else {
            changePassword(password);
            view.passwordChanged();
        }
    }


    @Override
    public @NotNull EditUserModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setData(model.getUser());
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    @Override
    public @NotNull EditUserView getView() {
        return view;
    }

    void doAction() {
        User data = view.getData(model.getUser());
        switch (model.getMode()) {
            case EDIT:
                if (!data.getUsername().equals(model.getUser().getUsername())) {
                    if (model.usernameExists(data.getUsername())) {
                        view.usernameAlreadyExists();
                        return;
                    }
                }
                break;
            case ADD:
                System.out.println(view.validate());
                if (model.usernameExists(data.getUsername())) {
                    view.usernameAlreadyExists();
                    return;
                }
                if (data.getPassword() == null) {
                    requestChangePassword();
                }
                break;
        }
        if (model.doAction(data)) {
            view.back();
        }
    }

    void openJobSelector() {
        new SelectorController<>("Ausgewählte Jobs", model.getUser().getJobs(), Job::defaultSearch,
                                 Column.create("Name", Job::getName, Key.JOB_NAME_READ),
                                 Column.create("Beschreibung", Job::getDescription, Key.JOB_DESCRIPTION_READ)
        ).openAsWindow(getView().getWindow(),
                              SubWindow::new);
    }

    void openPermissionSelector(){
        new SelectorController<>("Ausgewählte Berechtigungen", model.getUser().getPermissions(),
                                 Permission::defaultSearch,
                                 Column.create("Name", Permission::getName, Key.PERMISSION_NAME_READ)
        ).openAsWindow(getView().getWindow(),
                       SubWindow::new);
    }
}
