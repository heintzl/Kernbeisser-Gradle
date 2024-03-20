package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class IgnoredDialogField {
public static FieldIdentifier<IgnoredDialog,Long> id = new FieldIdentifier<>(IgnoredDialog.class, Long.class, "id");
public static FieldIdentifier<IgnoredDialog,User> user = new FieldIdentifier<>(IgnoredDialog.class, User.class, "user");
public static FieldIdentifier<IgnoredDialog,String> origin = new FieldIdentifier<>(IgnoredDialog.class, String.class, "origin");

}