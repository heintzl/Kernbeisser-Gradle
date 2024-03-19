package kernbeisser.DBEntities.Types;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Tasks.Catalog.Merge.MappedDifference;

public class IgnoredDifferenceField {
  public static FieldIdentifier<IgnoredDifference, Long> id =
      new FieldIdentifier<>(IgnoredDifference.class, "id");
  public static FieldIdentifier<IgnoredDifference, Article> article =
      new FieldIdentifier<>(IgnoredDifference.class, "article");
  public static FieldIdentifier<IgnoredDifference, MappedDifference> difference =
      new FieldIdentifier<>(IgnoredDifference.class, "difference");
  public static FieldIdentifier<IgnoredDifference, String> original =
      new FieldIdentifier<>(IgnoredDifference.class, "original");
  public static FieldIdentifier<IgnoredDifference, String> ignoredChange =
      new FieldIdentifier<>(IgnoredDifference.class, "ignoredChange");
}
