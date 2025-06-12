package kernbeisser.Windows.PermissionManagement;

import kernbeisser.Useful.Named;
import lombok.Getter;

public enum AccessLevel implements Named {
  NONE("Keine"),
  READ("Lesen"),
  READ_WRITE("Lesen & Schreiben"),
  NO_ACTION("Nein"),
  ACTION("Ja");

  @Getter private final String name;

  AccessLevel(String name) {
    this.name = name;
  }
}
