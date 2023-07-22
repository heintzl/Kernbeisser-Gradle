package kernbeisser.VersionIntegrationTools;

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
}
