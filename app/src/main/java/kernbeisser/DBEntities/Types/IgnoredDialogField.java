package kernbeisser.DBEntities.Types;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class IgnoredDialogField {
  public static FieldIdentifier<IgnoredDialog, Long> id =
      new FieldIdentifier<>(IgnoredDialog.class, "id");
  public static FieldIdentifier<IgnoredDialog, User> user =
      new FieldIdentifier<>(IgnoredDialog.class, "user");
  public static FieldIdentifier<IgnoredDialog, String> origin =
      new FieldIdentifier<>(IgnoredDialog.class, "origin");
}
