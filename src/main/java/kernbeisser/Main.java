package kernbeisser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.Config;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.StartUp.DataImport.DataImportController;
import kernbeisser.Useful.Tools;
import kernbeisser.VersionIntegrationTools.Version;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import lombok.Cleanup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  public static final Logger logger = LogManager.getLogger(Main.class);

  /**
   * sets the Look and Feel to Windows standard, sets the Image path, checks all needed Tables and
   * PriceLists and as least shows the LogIn Window
   */
  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    Config.safeFile();
    Locale.setDefault(Locale.GERMAN);
    logger.info("Free memory at start " + Runtime.getRuntime().freeMemory() / 1048576 + "MB");
    // Runs the jar with more memory if not enough is reserved
    checkRequiredMemory(args);
    DBConnection.getEntityManager();
    buildEnvironment();
    checkVersion();
    if (!Setting.DB_INITIALIZED.getBooleanValue()) {
      SwingUtilities.invokeLater(() -> new DataImportController().openTab());
    } else {
      openLogIn();
    }
  }

  public static String getPath() {
    try {
      return Main.class
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI()
          .getPath()
          .replace('/', File.separator.charAt(0))
          .substring(1);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void checkRequiredMemory(String[] args) {
    if (args.length == 0 && Runtime.getRuntime().maxMemory() / 1024 / 1024 < 980) {
      restart("-Xmx1024m");
    }
  }

  public static void restart(String arg) {
    try {
      Main.logger.info("Restarting Jar...");
      Process proc =
          Runtime.getRuntime()
              .exec("java " + (arg == null ? "" : arg) + " -jar " + getPath() + " restart");

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

      BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

      String s;
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }

      while ((s = stdError.readLine()) != null) {
        System.out.println(s);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.exit(-1);
  }

  public static void checkVersion() {
    Version.checkAndUpdateVersion();
  }

  public static void buildEnvironment() throws UnsupportedLookAndFeelException {
    logger.info("setting look and feel");
    setSettingLAF();
    logger.info("register FontAwesome");
    IconFontSwing.register(FontAwesome.getIconFont());
    try {
      Tools.scaleLabelSize(Float.parseFloat(Setting.LABEL_SCALE_FACTOR.getStringValue()));
    } catch (NumberFormatException ignored) {
    }
  }

  public static void setSettingLAF() throws UnsupportedLookAndFeelException {
    UIManager.setLookAndFeel(Setting.DEFAULT_THEME.getEnumValue(Theme.class).getLookAndFeel());
  }

  private static void openLogIn() {
    ((Frame) TabbedPaneModel.getMainPanel().getContainer()).setTitle("Kernbeißer (Login)");
    new SimpleLogInController().openTab();
  }

  private static void createTestJobs(int count) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (int i = 0; i < count; i++) {
      Job j = new Job();
      j.setDescription("Test Description: " + i);
      j.setName("Test Job: " + i);
      em.persist(j);
    }
    em.flush();
    et.commit();
  }

  public static void generateKeySet(Class<?> clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
        String base =
            toEnumName(clazz.getSimpleName()).replaceFirst("_", "")
                + "_"
                + toEnumName(field.getName());
        System.out.println(base + "_READ(" + clazz.getSimpleName() + ".class),");
        System.out.println(base + "_WRITE(" + clazz.getSimpleName() + ".class),");
      }
    }
  }

  public static String toEnumName(String s) {
    char[] charArray = s.toCharArray();
    Collection<String> parts = new ArrayList<>();
    int before = 0;
    for (int i = 0; i < charArray.length; i++) {
      char c = charArray[i];
      if (Character.isUpperCase(c)) {
        parts.add(s.substring(before, i));
        before = i;
      }
    }
    parts.add(s.substring(before, charArray.length));
    StringBuilder sb = new StringBuilder();
    parts.forEach(e -> sb.append(e.toUpperCase()).append("_"));
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
}
