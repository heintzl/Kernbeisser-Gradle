package kernbeisser.VersionIntegrationTools.UpdatingTools;

import kernbeisser.VersionIntegrationTools.Version;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;

public class BaseVersion implements VersionUpdatingTool {

  @Override
  public void runIntegration() {}

  @Override
  public Version getVersion() {
    return Version.BASE_VERSION;
  }
}
