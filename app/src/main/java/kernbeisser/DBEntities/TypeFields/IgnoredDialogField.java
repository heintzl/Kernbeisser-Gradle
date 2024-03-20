package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class IgnoredDialogField {
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDialog,Long> id = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDialog.class, Long.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDialog,kernbeisser.DBEntities.User> user = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDialog.class, kernbeisser.DBEntities.User.class, "user");
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDialog,java.lang.String> origin = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDialog.class, java.lang.String.class, "origin");

}