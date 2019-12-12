package kernbeisser.Windows.UserUI;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.UserPersistFeedback;
import kernbeisser.Windows.Controller;

import java.util.function.Consumer;

public class UserUIController implements Controller {
    private UserUIView view;
    private UserUIModel model;
    UserUIController(UserUIView view, Consumer<UserPersistFeedback> feedback){
        this.view=view;
        model=new UserUIModel(feedback);
    }

    void loadUser(User user){
        view.setData(user);
        model.setLoaded(user);
    }

    User getUser(){
        User out = model.getLoaded();
        view.getData(out);
        return out;
    }

    void changePassword(String to){
        model.getLoaded().setPassword(BCrypt.withDefaults().hashToString(12,to.toCharArray()));
    }


    @Override
    public UserUIModel getModel() {
        return model;
    }

    @Override
    public UserUIView getView() {
        return view;
    }
}
