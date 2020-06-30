package kernbeisser.Windows.EditUsers;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditUsers extends ObjectViewController<User> {
    public EditUsers() {
        super((user, mode) -> new EditUserController(user, mode), User::defaultSearch,
              Column.create("Vorname", User::getFirstName, PermissionKey.USER_FIRST_NAME_READ),
              Column.create("Nachname", User::getSurname, PermissionKey.USER_SURNAME_READ),
              Column.create("Benutzername", User::getUsername, PermissionKey.USER_USERNAME_READ),
              Column.create("Guthaben",u -> String.format("%.2f€",u.getUserGroup().getValue()), PermissionKey.USER_USER_GROUP_READ,
                            PermissionKey.USER_GROUP_VALUE_READ)
        );
    }
}
