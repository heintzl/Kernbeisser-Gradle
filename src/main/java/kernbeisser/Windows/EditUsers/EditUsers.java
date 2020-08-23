package kernbeisser.Windows.EditUsers;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditUsers extends ObjectViewController<User> {
  public EditUsers() {
    super(
        (user, mode) -> new EditUserController(user, mode),
        User::defaultSearch,
        false,
        Column.create("Vorname", User::getFirstName),
        Column.create("Nachname", User::getSurname),
        Column.create("Benutzername", User::getUsername),
        Column.create("Guthaben", u -> String.format("%.2f€", u.getUserGroup().getValue())));
  }
}
