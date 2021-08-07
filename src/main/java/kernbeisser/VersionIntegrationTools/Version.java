package kernbeisser.VersionIntegrationTools;

import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import kernbeisser.VersionIntegrationTools.UpdatingTools.BaseVersion;

public enum Version {
  BASE_VERSION(BaseVersion.class);
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
    if (tool.getVersion() != this) {
      throw new VersionUpdatingException(
          "The Version integration tool of " + this + " doesn't refer to correct enum value");
    }
    try {
      tool.runIntegration();
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
      Setting.DB_VERSION.changeValue(version.name());
    }
  }

  public static void checkAndUpdateVersion() {
    updateFrom(Setting.DB_VERSION.getEnumValue(Version.class));
  }
}
