package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class IgnoredDifferenceField {
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDifference,Long> id = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDifference.class, Long.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDifference,kernbeisser.DBEntities.Article> article = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDifference.class, kernbeisser.DBEntities.Article.class, "article");
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDifference,kernbeisser.Tasks.Catalog.Merge.MappedDifference> difference = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDifference.class, kernbeisser.Tasks.Catalog.Merge.MappedDifference.class, "difference");
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDifference,java.lang.String> original = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDifference.class, java.lang.String.class, "original");
public static FieldIdentifier<kernbeisser.DBEntities.IgnoredDifference,java.lang.String> ignoredChange = new FieldIdentifier<>(kernbeisser.DBEntities.IgnoredDifference.class, java.lang.String.class, "ignoredChange");

}