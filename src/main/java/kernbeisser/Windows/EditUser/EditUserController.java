package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Security.Proxy;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Selector.SelectorController;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class EditUserController implements Controller<EditUserView,EditUserModel> {
    private final EditUserView view;
    private final EditUserModel model;

    public EditUserController(User user, Mode mode) {
        model = new EditUserModel(mode == Mode.ADD ? Proxy.getSecureInstance(new User()) : user == null ? Proxy.getSecureInstance(new User()) : user, mode);
        if (mode == Mode.REMOVE) {
            model.doAction(model.getUser());
            view = null;
        } else {
            this.view = new EditUserView();
        }
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
    public void fillUI() {}

    @Override
    public Key[] getRequiredKeys() {
        return new Key[]{
                Key.USER_USERNAME_READ,
                };
    }

    @Override
    public @NotNull EditUserView getView() {
        return view;
    }

    void doAction() {
        User data;
        try {
            data = view.getObjectForm().getData();
        } catch (CannotParseException e) {
            view.invalidInput();
            view.getObjectForm().markErrors();
            return;
        }
        switch (model.getMode()) {
            case ADD:
                if (!view.getObjectForm().isValid()) {
                    view.invalidInput();
                    view.getObjectForm().markErrors();
                    return;
                }
                if (model.usernameExists(data.getUsername())) {
                    view.usernameAlreadyExists();
                    return;
                }
                try {
                    if (data.getPassword().equals("")) {
                        requestChangePassword();
                    }
                } catch (AccessDeniedException e) {
                    e.printStackTrace();
                }
                break;
        }
        if (model.doAction(data)) {
            view.back();
        }
    }

    void refreshUsername(){
        if(model.getMode()==Mode.ADD) {
            User data = view.getObjectForm().getDataIgnoreWrongInput();
            if (data.getSurname() != null && data.getFirstName() != null) {
                view.getObjectForm()
                    .getOriginal()
                    .setUsername(model.generateUsername(data.getFirstName().toLowerCase().replace(" ", ""),
                                                        data.getSurname().toLowerCase()).replace(" ", ""));
                view.getObjectForm().pullData();
            }
            view.setUsername(view.getObjectForm().getOriginal().getUsername());
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
