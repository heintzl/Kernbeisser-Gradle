package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.util.Set;

public class PermissionField {
public static FieldIdentifier<Permission,Integer> id = new FieldIdentifier<>(Permission.class, Integer.class, "id");
public static FieldIdentifier<Permission,String> name = new FieldIdentifier<>(Permission.class, String.class, "name");
public static FieldIdentifier<Permission,Set> keySet = new FieldIdentifier<>(Permission.class, Set.class, "keySet");

}