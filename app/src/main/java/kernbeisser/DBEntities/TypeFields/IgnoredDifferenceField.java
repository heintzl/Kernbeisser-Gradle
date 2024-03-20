package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Tasks.Catalog.Merge.MappedDifference;

public class IgnoredDifferenceField {
public static FieldIdentifier<IgnoredDifference,Long> id = new FieldIdentifier<>(IgnoredDifference.class, Long.class, "id");
public static FieldIdentifier<IgnoredDifference,Article> article = new FieldIdentifier<>(IgnoredDifference.class, Article.class, "article");
public static FieldIdentifier<IgnoredDifference, MappedDifference> difference = new FieldIdentifier<>(IgnoredDifference.class, MappedDifference.class, "difference");
public static FieldIdentifier<IgnoredDifference,String> original = new FieldIdentifier<>(IgnoredDifference.class, String.class, "original");
public static FieldIdentifier<IgnoredDifference,String> ignoredChange = new FieldIdentifier<>(IgnoredDifference.class, String.class, "ignoredChange");

}