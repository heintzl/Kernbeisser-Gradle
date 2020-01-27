package kernbeisser.Windows.EditUsers;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

public class EditUsers extends ObjectViewController<User> {
    public EditUsers(Window current){
        super(current,EditUserController::new,() -> User.getAll("order by firstName ASC"),
                Column.create("Vorname",User::getFirstName),
                Column.create("Nachname",User::getSurname),
                Column.create("Username",User::getSurname)
                );
    }
}
