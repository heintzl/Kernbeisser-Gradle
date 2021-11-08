package kernbeisser.VersionIntegrationTools.UpdatingTools;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.DelegatingMap;
import kernbeisser.VersionIntegrationTools.Version;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;
import lombok.SneakyThrows;

public class PermissionKeyChange implements VersionUpdatingTool {

  @SneakyThrows
  @Override
  public void runIntegration() {
    Field field = Class.class.getDeclaredField("enumConstantDirectory");
    field.setAccessible(true);
    PermissionKey.valueOf(PermissionKey.ACTION_LOGIN.name());
    Map<String, PermissionKey> map = (Map<String, PermissionKey>) field.get(PermissionKey.class);
    Map<String, PermissionKey> delegatingMap =
        new DelegatingMap<String, PermissionKey>(map) {
          @Override
          public PermissionKey get(Object key) {
            PermissionKey result = super.get(key);
            if (result == null) {
              Version.logger.info("replacing " + key + " with deprecated");
              return PermissionKey.DEPRECATED;
            }
            return result;
          }
        };
    field.set(PermissionKey.class, delegatingMap);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<Permission> permissions =
        em.createQuery("select p from Permission p", Permission.class).getResultList();
    for (Permission permission : permissions) {
      if (permission.getKeySet().remove(PermissionKey.DEPRECATED)) {
        permission.setKeySet(new HashSet<>(permission.getKeySet()));
        em.persist(permission);
      }
    }
    em.flush();
    field.set(PermissionKey.class, map);
  }
}
