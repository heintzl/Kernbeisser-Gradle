package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Useful.DelegatingMap;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import rs.groump.PermissionKey;

@Log
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
              log.info("replacing " + key + " with deprecated");
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
