package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class PermissionField {
  public static FieldIdentifier<kernbeisser.DBEntities.Permission, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.Permission.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.Permission, java.lang.String> name =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Permission.class, java.lang.String.class, "name");
  public static FieldIdentifier<kernbeisser.DBEntities.Permission, java.util.Set> keySet =
      new FieldIdentifier<>(kernbeisser.DBEntities.Permission.class, java.util.Set.class, "keySet");
}
