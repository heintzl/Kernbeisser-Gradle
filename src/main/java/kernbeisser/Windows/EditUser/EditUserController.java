package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JobSelector.JobSelectorController;
import kernbeisser.Windows.JobSelector.JobSelectorView;
import kernbeisser.Windows.Window;

public class EditUserController implements Controller {
    private EditUserView view;
    private EditUserModel model;
    public EditUserController(Window current, User user, Mode mode){
        this.view=new EditUserView(this,current);
        model=new EditUserModel(user == null ? new User() : user,mode);
        view.setData(model.getUser());
    }

    void changePassword(String to){
        model.getUser().setPassword(BCrypt.withDefaults().hashToString(12,to.toCharArray()));
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
        if(model.doAction(view.getData(model.getUser())))view.back();
    }

    void openJobSelector() {
        new JobSelectorController(this.view, model.getUser().getJobs());
    }
}
