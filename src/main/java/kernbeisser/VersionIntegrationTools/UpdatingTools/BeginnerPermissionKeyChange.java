package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.HashMap;
import kernbeisser.Config.Config;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

public class BeginnerPermissionKeyChange implements VersionUpdatingTool {

  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(
            "UPDATE Permission_keySet SET keySet = 'ACTION_ADD_TRIAL_MEMBER' "
                + "WHERE keySet = 'ACTION_ADD_BEGINNER'")
        .executeUpdate();

    HashMap<String, String> reportConfig = Config.getConfig().getReports().getReports();
    if (!reportConfig.containsKey("trialMemberReportFileName")) {
      reportConfig.put("trialMemberReportFileName", "Probemitglieder.jrxml");
      Config.safeFile();
    }
  }
}
