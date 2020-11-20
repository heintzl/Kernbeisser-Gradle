package kernbeisser.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// May replace ConfigManager with this
@Data
public final class Config {

  private static final Path CONFIG_PATH = Paths.get("config.json");

  @Getter(AccessLevel.NONE)
  private static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

  @Getter(AccessLevel.NONE)
  public static Logger logger = LogManager.getLogger(Config.class);

  @Getter(lazy = true, value = AccessLevel.PUBLIC)
  private static final Config config = loadJSON();

  public static void safeFile() {
    safeInFile(getConfig());
  }

  public static void safeInFile(Config config) {
    try {
      Files.write(CONFIG_PATH, Collections.singleton(gson.toJson(config)));
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  @SneakyThrows
  private static Config loadJSON() {
    try {
      if (CONFIG_PATH.toFile().createNewFile()) {
        Config config = new Config();
        safeInFile(config);
        return config;
      }
      return gson.fromJson(Files.lines(CONFIG_PATH).collect(Collectors.joining()), Config.class);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
      throw e;
    }
  }

  // Config fields

  private File imagePath = new File("images");

  private boolean dbIsInitialized = false;

  @Setter private DBAccess dbAccess = new DBAccess();

  private Reports reports = new Reports();

  public DBAccess getDBAccessData() {
    DBAccess dbAccess = getConfig().getDbAccess();
    if (dbAccess.getPassword() == null || dbAccess.getPassword().replace(" ", "").equals("")) {
      logger.debug("No DBAccess.Password found in config file.");
      logger.info("Using embedded password for DBConnection.");
      dbAccess.setPassword(new String(IgnoreThis.ignoreMe));
    }
    return dbAccess;
  }

  @Getter
  @Setter(AccessLevel.PACKAGE)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DBAccess {
    private String url = "";
    private String username = "";
    private String password = "";
  }

  @Data
  public static class Reports {
    private File reportDirectory = new File(".");
    private File outputDirectory = new File(".");
    private HashMap<String, String> reports = new HashMap<>();
  }
}
