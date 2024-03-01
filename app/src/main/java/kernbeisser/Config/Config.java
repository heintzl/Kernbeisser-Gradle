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
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

// May replace ConfigManager with this
@Data
@Log4j2
public final class Config {

  private static Path configPath = Paths.get("config.json");

  @Getter(AccessLevel.NONE)
  private static final Gson gson =
      new GsonBuilder()
          .serializeNulls()
          .registerTypeAdapter(File.class, new FileTypeAdapter())
          .setPrettyPrinting()
          .create();

  @Getter(lazy = true, value = AccessLevel.PUBLIC)
  private static final Config config = loadJSON();

  public static void safeFile() {
    safeInFile(getConfig());
  }

  public static void safeFile(String[] args) {
    for (String arg : args) {
      if (arg.startsWith("-configFile:")) {
        String configFile = arg.split(":")[1];
        configPath = Paths.get(configFile);
        log.info("Using custom configuration from " + configFile);
        break;
      }
    }
    safeInFile(getConfig());
  }

  public static void safeInFile(@NotNull Config config) {
    try {
      Files.write(configPath, Collections.singleton(gson.toJson(config)));
    } catch (IOException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }

  @SneakyThrows
  private static Config loadJSON() {
    try {
      if (configPath.toFile().createNewFile()) {
        Config config = new Config();
        safeInFile(config);
        return config;
      }
      return gson.fromJson(Files.lines(configPath).collect(Collectors.joining()), Config.class);
    } catch (IOException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }

  // Config fields

  private File imagePath = new File("images");

  private File defaultKornkraftInboxDir = new File("USB:\\kornkraft");

  private File defaultBnnInboxDir = new File("USB:\\bnn");

  @Getter(AccessLevel.PRIVATE)
  private DBAccess dbAccess = new DBAccess();

  public void setDbAccess(DBAccess dbAccess) {
    dbAccess.setPassword(dbAccess.getPassword().replace(new String(IgnoreThis.ignoreMe), ""));
    this.dbAccess = dbAccess;
  }

  private Reports reports = new Reports();

  private Preorders preorders = new Preorders();

  public DBAccess getDBAccessData() {
    DBAccess dbAccess = getConfig().getDbAccess();
    if (dbAccess.getPassword() == null || dbAccess.getPassword().replace(" ", "").equals("")) {
      log.debug("No DBAccess.Password found in config file.");
      log.info("Using embedded password for DBConnection.");
      dbAccess.setPassword(new String(IgnoreThis.ignoreMe));
    }
    if (dbAccess.getEncoding().replace(" ", "").isEmpty()) {
      log.debug("No DBAccess.Encoding found in config file.");
      log.info("Using UTF-8 encoding for DBConnection.");
      dbAccess.setEncoding("UTF-8");
    }
    return dbAccess;
  }

  @Getter
  @Setter(AccessLevel.PUBLIC)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DBAccess {
    private String url = "";
    private String username = "";
    private String password = "";
    private String encoding = "";
  }

  @Data
  public static class Reports {
    private File reportDirectory = new File(".");
    private File outputDirectory = new File("../output");
    private HashMap<String, String> reports = new HashMap<>();
  }

  @Data
  public static class Preorders {
    private File exportDirectory = new File("../preorder");
  }
}
