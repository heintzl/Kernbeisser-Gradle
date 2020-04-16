package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Selector.SelectorController;
import org.jetbrains.annotations.NotNull;

public class EditUserController implements Controller<EditUserView,EditUserModel> {
    private EditUserView view;
    private EditUserModel model;

    public EditUserController(User user, Mode mode) {
        model = new EditUserModel(user == null ? new User() : user, mode);
        if (mode == Mode.REMOVE) {
            model.doAction(user);
            return;
        } else {
            this.view = new EditUserView(this);
        }
    }

    private void changePassword(String to) {
        model.getUser().setPassword(BCrypt.withDefaults().hashToString(12, to.toCharArray()));
    }

    void requestChangePassword() {
        String password = view.requestPassword();
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
        new SelectorController<Job>("Ausgewählte Jobs", model.getUser().getJobs(), Job::defaultSearch,
                                    Column.create("Name", Job::getName, Key.JOB_NAME_READ),
                                    Column.create("Beschreibung", Job::getDescription, Key.JOB_DESCRIPTION_READ));
    }

    void openPermissionSelector(){
        new SelectorController<Permission>("Ausgewählte Berechtigungen",model.getUser().getPermissions(), Permission::defaultSearch,
                                 Column.create("Name", Permission::getName, Key.PERMISSION_NAME_READ)
                             );
    }
}
