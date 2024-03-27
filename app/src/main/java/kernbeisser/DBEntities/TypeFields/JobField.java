package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class JobField {
  public static FieldIdentifier<kernbeisser.DBEntities.Job, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.Job.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.Job, java.lang.String> name =
      new FieldIdentifier<>(kernbeisser.DBEntities.Job.class, java.lang.String.class, "name");
  public static FieldIdentifier<kernbeisser.DBEntities.Job, java.lang.String> description =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Job.class, java.lang.String.class, "description");
  public static FieldIdentifier<kernbeisser.DBEntities.Job, java.time.Instant> createDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Job.class, java.time.Instant.class, "createDate");
  public static FieldIdentifier<kernbeisser.DBEntities.Job, java.time.Instant> updateDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Job.class, java.time.Instant.class, "updateDate");
}
