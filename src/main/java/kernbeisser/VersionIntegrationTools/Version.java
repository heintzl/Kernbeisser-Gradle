package kernbeisser.VersionIntegrationTools;

import java.util.function.Supplier;
import kernbeisser.DBEntities.SystemSetting;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.VersionIntegrationTools.UpdatingTools.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Version {
  BASE_VERSION(BaseVersion::new),
  UNUSED_PERMISSION_KEY_REMOVING(PermissionKeyChange::new),
  REFACTOR_DB_VERSIONING(RemoveDeprectatedSettings::new),
  TRIAL_MEMBERSHIP(BeginnerPermissionKeyChange::new),
  SAVE_TRANSACTIONREPORT_NO(AddTransactionReportNo::new),
  TEST_USERS(FillTestUserFlag::new),
  NEW_ARTICLE_PROPERTIES(AddArticleSupplyPermissions::new),
  CATALOG_IMPORT(AddCatalogImportPermission::new),
  SALE_SESSION_CLOSE_POPUP(AddSaleSessionClosePermission::new),
  PREORDER_FROM_CATALOG(MigrateOpenPreOrders::new);

  public static final Logger logger = LogManager.getLogger(Version.class);
  private final Supplier<VersionUpdatingTool> versionUpdatingToolSupplier;

  Version(Supplier<VersionUpdatingTool> versionUpdatingToolSupplier) {
    this.versionUpdatingToolSupplier = versionUpdatingToolSupplier;
  }

  public static Version newestVersion() {
    Version[] versions = values();
    return versions[versions.length - 1];
  }

  private void runUpdate() {
    try {
      Access.runWithAccessManager(
          AccessManager.NO_ACCESS_CHECKING, versionUpdatingToolSupplier.get()::runIntegration);
    } catch (NullPointerException e) {
      throw new VersionUpdatingException("Nullptr. Exception while updating version", e);
    }
  }

  public static void updateFrom(Version version) {
    Version[] versions = Version.values();
    for (int i = version.ordinal() + 1; i < versions.length; i++) {
      versions[i].runUpdate();
      SystemSetting.setValue(SystemSetting.DB_VERSION, versions[i].name());
    }
  }

  public static void checkAndUpdateVersion() {
    updateFrom(Version.valueOf(SystemSetting.getValue(SystemSetting.DB_VERSION)));
  }
}
