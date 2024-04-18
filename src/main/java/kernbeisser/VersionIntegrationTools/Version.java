package kernbeisser.VersionIntegrationTools;

import kernbeisser.DBEntities.SystemSetting;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Useful.Tools;
import kernbeisser.VersionIntegrationTools.UpdatingTools.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Version {
  BASE_VERSION(BaseVersion.class),
  UNUSED_PERMISSION_KEY_REMOVING(PermissionKeyChange.class),
  REFACTOR_DB_VERSIONING(RemoveDeprectatedSettings.class),
  TRIAL_MEMBERSHIP(BeginnerPermissionKeyChange.class),
  SAVE_TRANSACTIONREPORT_NO(AddTransactionReportNo.class),
  TEST_USERS(FillTestUserFlag.class),
  NEW_ARTICLE_PROPERTIES(AddArticleSupplyPermissions.class),
  CATALOG_IMPORT(AddCatalogImportPermission.class),
  SALE_SESSION_CLOSE_POPUP(AddSaleSessionClosePermission.class),
  PREORDER_FROM_CATALOG(MigrateOpenPreOrders.class),
  CONFIRMATION_PANEL(AddOnShoppingMaskCheckoutPermission.class);

  public static final Logger logger = LogManager.getLogger(Version.class);
  private final Class<? extends VersionUpdatingTool> updatingToolClass;

  Version(Class<? extends VersionUpdatingTool> updatingToolClass) {
    this.updatingToolClass = updatingToolClass;
  }

  public static Version newestVersion() {
    Version[] versions = values();
    return versions[versions.length - 1];
  }

  private void runUpdate() {
    VersionUpdatingTool tool = createTool();
    try {
      Access.runWithAccessManager(AccessManager.NO_ACCESS_CHECKING, tool::runIntegration);
    } catch (NullPointerException e) {
      throw new VersionUpdatingException("Nullptr. Exception while updating version", e);
    }
  }

  private VersionUpdatingTool createTool() {
    return Tools.createWithoutConstructor(updatingToolClass);
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
