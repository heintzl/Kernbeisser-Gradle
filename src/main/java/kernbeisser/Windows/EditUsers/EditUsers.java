package kernbeisser.Windows.EditUsers;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

public class EditUsers extends ObjectViewController<User> {
    public EditUsers(Window current){
        super(current,EditUserController::new,User::defaultSearch,
                Column.create("Vorname",User::getFirstName, Key.USER_FIRST_NAME_READ),
                Column.create("Nachname",User::getSurname, Key.USER_SURNAME_READ),
                Column.create("Benutzername",User::getUsername,Key.USER_USERNAME_READ)
                );
    }
}
