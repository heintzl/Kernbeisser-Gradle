package kernbeisser.DBEntities.Types;

import java.util.Set;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class PermissionField {
  public static FieldIdentifier<Permission, Integer> id =
      new FieldIdentifier<>(Permission.class, "id");
  public static FieldIdentifier<Permission, String> name =
      new FieldIdentifier<>(Permission.class, "name");
  public static FieldIdentifier<Permission, Set> keySet =
      new FieldIdentifier<>(Permission.class, "keySet");
}
