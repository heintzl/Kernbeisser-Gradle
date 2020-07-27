package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum UserPersistFeedback implements Named {
  ERROR("Es ist ein Unbekannter fehler aufgetreten"),
  UN_COMPLETE_USER("Der Nutzer ist unvollst\u00e4ndig"),
  SUCCESS("Die Ver\u00e4nderung wurde \u00fcbernommen"),
  USERNAME_TO_SHORT("Der Benutzername ist leider zu kurz"),
  USERNAME_ALREADY_EXISTS("Der gew\u00e4hlte Nutzername ist leider schon vergeben"),
  CANNOT_LEAF_USER_GROUP(
      "Die Nutzergruppe kann leider nicht verlassen werden,\nda kein weiter Nutzer in ihr ist"),
  NO_USER_GROUP_SELECTED(
      "Es ist keine Nutzergruppe ausgew\u00e4hlt bitte w\u00e4hlen sie eine aus"),
  NO_USER_SELECTED(
      "Es wurde kein Nutzer ausgew\u00e4hlt,\n bitte w\u00e4hlen sie einen Nutzer aus");

  final String message;

  UserPersistFeedback(String s) {
    this.message = s;
  }

  @Override
  public String getName() {
    return message;
  }

  @Override
  public String toString() {
    return message;
  }
}
