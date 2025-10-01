package kernbeisser.VersionIntegrationTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import lombok.Cleanup;
import rs.groump.PermissionKey;

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

  private void updateEnum(List<String> enumNames, String table, String column, String strategy) {
    String sql =
        "ALTER TABLE %s MODIFY COLUMN %s ENUM(%s)"
            .formatted(
                table,
                column,
                enumNames.stream().map("'%s'"::formatted).collect(Collectors.joining(", ")));
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery("SET SESSION alter_algorithm = :strategy")
        .setParameter("strategy", strategy)
        .executeUpdate();
    em.createNativeQuery(sql).executeUpdate();
  }

  default void updateEnum(List<String> enumNames, String table, String column) {
    updateEnum(enumNames, table, column, "INSTANT");
  }

  default void updatePermissionKeyset() {
    List<String> permissionNames =
        Arrays.stream(PermissionKey.values()).map(PermissionKey::name).toList();
    this.updateEnum(permissionNames, "Permission_keySet", "keySet", "COPY");
  }
}
