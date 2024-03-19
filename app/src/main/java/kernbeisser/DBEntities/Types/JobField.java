package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class JobField {
  public static FieldIdentifier<Job, Integer> id = new FieldIdentifier<>(Job.class, "id");
  public static FieldIdentifier<Job, String> name = new FieldIdentifier<>(Job.class, "name");
  public static FieldIdentifier<Job, String> description =
      new FieldIdentifier<>(Job.class, "description");
  public static FieldIdentifier<Job, Instant> createDate =
      new FieldIdentifier<>(Job.class, "createDate");
  public static FieldIdentifier<Job, Instant> updateDate =
      new FieldIdentifier<>(Job.class, "updateDate");
}
