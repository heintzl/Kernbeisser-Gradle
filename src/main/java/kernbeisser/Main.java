package kernbeisser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.ConfigManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.StartUp.DataImport.DataImportController;
import kernbeisser.Tasks.Catalog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  public static final Logger logger = LogManager.getLogger(Main.class);

  /**
   * sets the Look and Feel to Windows standard, sets the Image path, checks all needed Tables and
   * PriceLists and as least shows the LogIn Window
   */
  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    logger.info("Free memory at start "+Runtime.getRuntime().freeMemory()/1048576+"MB");
    buildEnvironment();
    checkVersion();
    if (!Setting.DB_INITIALIZED.getBooleanValue()) {
      SwingUtilities.invokeLater(() -> new DataImportController().openTab("Daten importieren"));
    } else {
      checkCatalog();
      Tools.activateKeyboardListener();
      openLogIn();
    }
  }

  public static void checkCatalog() {
    logger.info("Checking Catalog ...");
    if (ConfigManager.isCatalogUpToDate()) {
      logger.info("Catalog up to Date!");
    } else Catalog.updateCatalog();
  }

  public static void checkVersion() {
    logger.info(
        "Aktuelle DB Version: "
            + Setting.DB_VERSION.getStringValue()
            + " | Branch Version: "
            + Setting.DB_VERSION.getDefaultValue());
    if (!Setting.DB_VERSION.getStringValue().equals(Setting.DB_VERSION.getDefaultValue())
        && JOptionPane.showConfirmDialog(
                null,
                "Ihre Datenbankversion entspricht nicht der aktuellsten Version.\nAktuelle Version: "
                    + Setting.DB_VERSION.getStringValue()
                    + "\nNeuste Verstion: "
                    + Setting.DB_VERSION.getDefaultValue()
                    + "\nWollen sie die Datenbank leeren und eine neue Datenbank instanz\nerstellen?")
            == 0) {
      updateDBVersion();
    }
  }

  public static void updateDBVersion() {
    DBConnection.updateDatabase();
  }

  public static void buildEnvironment() throws UnsupportedLookAndFeelException {
    logger.info("setting look and feel");
    setSettingLAF();
    logger.info("register FontAwesome");
    IconFontSwing.register(FontAwesome.getIconFont());
  }

  public static void setSettingLAF() throws UnsupportedLookAndFeelException {
    UIManager.setLookAndFeel(Setting.DEFAULT_THEME.getEnumValue(Theme.class).getLookAndFeel());
  }

  private static void openLogIn() {
    new SimpleLogInController().openTab("Log In");
  }

  private static void createTestJobs(int count) {
    EntityManager em = DBConnection.getEntityManager();
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
    em.close();
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
