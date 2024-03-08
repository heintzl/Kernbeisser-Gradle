package kernbeisser.Config;

import java.nio.charset.StandardCharsets;

public class IgnoreThis {
  public static Object TEST_FLAG;

  public static String ignoreIt = "KB29.06.2019CreateDate";

  public static byte[] ignoreMe = ignoreIt.getBytes(StandardCharsets.UTF_8);

  public static byte[] ignoreIt() {
    return ignoreMe;
  }
}
