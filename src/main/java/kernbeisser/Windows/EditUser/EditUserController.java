package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JobSelector.JobSelectorController;
import kernbeisser.Windows.JobSelector.JobSelectorView;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class EditUserController implements Controller {
    private EditUserView view;
    private EditUserModel model;
    public EditUserController(Window current, User user, Mode mode){
        this.view=new EditUserView(this,current);
        model=new EditUserModel(user == null ? new User() : user,mode);
        view.setPermissions(model.getAllPermission());
        view.setData(model.getUser());
    }

    private void changePassword(String to){
        model.getUser().setPassword(BCrypt.withDefaults().hashToString(12,to.toCharArray()));
    }

    void requestChangePassword(){
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
    public EditUserModel getModel() {
        return model;
    }

    @Override
    public EditUserView getView() {
        return view;
    }

    void doAction() {
        User data = view.getData(model.getUser());
        switch (model.getMode()) {
            case EDIT:
                if(!data.getUsername().equals(model.getUser().getUsername()))
                    if(model.usernameExists(data.getUsername())){
                        view.usernameAlreadyExists();
                        return;
                    }

            case ADD:
                if(model.usernameExists(data.getUsername())){
                    view.usernameAlreadyExists();
                    return;
                }
                if(data.getPassword()==null)
                    requestChangePassword();
        }
        if(model.doAction(data))view.back();
    }

    void openJobSelector() {
        new JobSelectorController(this.view, model.getUser().getJobs());
    }
}
