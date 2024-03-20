package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class JobField {
public static FieldIdentifier<Job,Integer> id = new FieldIdentifier<>(Job.class, Integer.class, "id");
public static FieldIdentifier<Job,String> name = new FieldIdentifier<>(Job.class, String.class, "name");
public static FieldIdentifier<Job,String> description = new FieldIdentifier<>(Job.class, String.class, "description");
public static FieldIdentifier<Job,Instant> createDate = new FieldIdentifier<>(Job.class, Instant.class, "createDate");
public static FieldIdentifier<Job,Instant> updateDate = new FieldIdentifier<>(Job.class, Instant.class, "updateDate");

}