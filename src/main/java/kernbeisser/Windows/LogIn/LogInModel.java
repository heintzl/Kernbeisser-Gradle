package kernbeisser.Windows.LogIn;

import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Model;

public class LogInModel implements Model {
    private static User loggedIn;
    public static User getLoggedInUser(){
        return loggedIn;
    }
    void setLoggedIn(User user){
        loggedIn=user;
    }
    User getLoggedIn(){
        return loggedIn;
    }
}
