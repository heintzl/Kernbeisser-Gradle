package kernbeisser.VersionIntegrationTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import lombok.Cleanup;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface VersionUpdatingTool {

  void runIntegration();

  String renameKeySetQuery = "UPDATE Permission_keySet SET keySet = :to WHERE keySet = :from";
  String insertRwKeySetPairQuery =
      "INSERT INTO Permission_keySet (Permission_id, keySet) "
          + "SELECT Permission_id, REPLACE(keySet, :template, :new) "
          + "FROM Permission_keySet WHERE keySet LIKE CONCAT(:template, '_%')";
  String insertSimpleKeySetQuery =
      "INSERT INTO Permission_keySet (Permission_id, keySet) "
          + "SELECT Permission_id, :new "
          + "FROM Permission_keySet WHERE keySet = :template";

  default void updateEnum(List<String> enumNames, String table, String column) {
    String sql = "ALTER TABLE %s MODIFY COLUMN %s ENUM(%s)".formatted(
            table,
            column,
            enumNames.stream().map("'%s'"::formatted).collect(Collectors.joining(", ")));
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery("SET SESSION alter_algorithm='INSTANT';").executeUpdate();
    em.createNativeQuery(sql).executeUpdate();

  }
}
