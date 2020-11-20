package kernbeisser.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import kernbeisser.Useful.Tools;
import lombok.SneakyThrows;

// May replace ConfigManager with this
public final class Config {

  private static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

  private static final Config config = new Config(); // load();

  @SneakyThrows
  private static Config load() {
    try {
      File file = new File("newConfig.json");
      if (file.exists()) {
        return gson.fromJson((new FileReader(file)), Config.class);
      } else {
        assert file.createNewFile();
        FileWriter fw = new FileWriter(file, false);
        fw.write(gson.toJson(new Config()));
        fw.flush();
        fw.close();
        return load();
      }
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
      throw e;
    }
  }

  public static Config getConfig() {
    return config;
  }

  private File imagePath = new File("images");

  private boolean dbIsInitialized = false;

  private DBAccess dbAccess = new DBAccess();

  private Reports reports = new Reports();

  public boolean isDbIsInitialized() {
    return dbIsInitialized;
  }

  public File getImagePath() {
    return imagePath;
  }

  public Reports getReports() {
    return reports;
  }

  public DBAccess getDbAccess() {
    return dbAccess;
  }

  public static class DBAccess {
    private String url = "";
    private String username = "";
    private String password = "";

    public String getUrl() {
      return url;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }
  }

  public static class Reports {
    private String reportDirectory = "";
    private String outputDirectory = "";
    private String invoiceFileName = "";

    public String getReportDirectory() {
      return reportDirectory;
    }

    public String getOutputDirectory() {
      return outputDirectory;
    }

    public String getInvoiceFileName() {
      return invoiceFileName;
    }
  }
}
