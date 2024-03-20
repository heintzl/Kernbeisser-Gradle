package kernbeisser;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Locale;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.Config;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.StartUp.SplashScreenHandler;
import kernbeisser.Useful.UiTools;
import kernbeisser.VersionIntegrationTools.Version;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.DBEntities.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import rs.groump.Agent;

@Log4j2
public class Main {

  /**
   * sets the Look and Feel to Windows standard, sets the Image path, checks all needed Tables and
   * PriceLists and as least shows the LogIn Window
   */
  private static void logUncaughtException(Thread t, Throwable e) {
    log.error(e.toString(), e);
  }

  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    printClassToFile(
            Article.class,
            ArticlePrintPool.class,
            Articles.class,
            ArticleStock.class,
            CatalogEntry.class,
            IgnoredDialog.class,
            IgnoredDifference.class,
            Job.class,
            Offer.class,
            Permission.class,
            Post.class,
            PreOrder.class,
            PriceList.class,
            Purchase.class,
            SaleSession.class,
            SettingValue.class,
            Shelf.class,
            ShoppingItem.class,
            Supplier.class,
            SurchargeGroup.class,
            SystemSetting.class,
            Transaction.class,
            User.class,
            UserGroup.class,
            UserSettingValue.class
    );
    if (!Agent.agentInitialized) {
      System.out.println("cannot run, security.jar is not linked as a java-agent");
      System.exit(-1);
    }
    Thread.setDefaultUncaughtExceptionHandler(Main::logUncaughtException);
    Config.safeFile(args);
    Locale.setDefault(Locale.GERMAN);
    log.info("Free memory at start " + Runtime.getRuntime().freeMemory() / 1048576 + "MB");
    // Runs the jar with more memory if not enough is reserved
    checkRequiredMemory(args);
    SplashScreenHandler splashScreenHandler =
        new SplashScreenHandler(140, 345, 14, Color.DARK_GRAY);
    splashScreenHandler.setSplashComment("Stelle Verbindung zur Datenbank her");
    DBConnection.getEntityManager();
    splashScreenHandler.setSplashComment("Initialisiere UI");
    buildEnvironment();
    splashScreenHandler.setSplashComment("Prüfe Datenbankversion");
    checkVersion();
    SwingUtilities.invokeLater(() -> openLogIn());
  }

  @SneakyThrows
  public static void printClassToFile(Class<?>... classes) {
    for (Class<?> clazz : classes) {
      FileWriter fw = new FileWriter(new File("gen/" + clazz.getSimpleName() + "Field.java"));
      String header =
          """
              package kernbeisser.DBEntities.Types;

              import kernbeisser.DBConnection.FieldIdentifier;
              import kernbeisser.DBEntities.*;

              import java.time.Instant;
              import java.util.Set;
              import java.util.Collection;
              import java.util.List;
              """;
      fw.write(header);
      fw.write("public class " + clazz.getSimpleName() + "Field {\n");
      for (Field declaredField : clazz.getDeclaredFields()) {
        if (Modifier.isStatic(declaredField.getModifiers())) {
          continue;
        }
        String statement =
            "public static FieldIdentifier<"
                + clazz.getSimpleName()
                + ","
                + getTypeName(declaredField.getType())
                + "> "
                + declaredField.getName()
                + " = new FieldIdentifier<>("
                + clazz.getSimpleName()
                + ".class, "
                    + getTypeName(declaredField.getType())
                    + ".class, \""
                + declaredField.getName()
                + "\");\n";
        fw.write(statement);
      }
      fw.write("\n}");
      fw.flush();
      fw.close();
    }
  }

  public static void printClass(Class<?> clazz) {}

  public static String getTypeName(Class<?> clazz) {
    if (!clazz.isPrimitive()) return clazz.getSimpleName();
    return switch (clazz.getSimpleName()) {
      case "double":
        yield "Double";
      case "int":
        yield "Integer";
      case "long":
        yield "Long";
      case "byte":
        yield "Byte";
      case "char":
        yield "Character";
      case "short":
        yield "Short";
      case "boolean":
        yield "Boolean";
      case "float":
        yield "Float";
      default:
        throw new UnsupportedOperationException("Type not supported");
    };
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
      Main.log.info("Restarting Jar...");
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
    log.info("setting look and feel");
    setSettingLAF();
    log.info("register FontAwesome");
    IconFontSwing.register(FontAwesome.getIconFont());
    try {
      UiTools.scaleFonts(Float.parseFloat(Setting.LABEL_SCALE_FACTOR.getStringValue()));
    } catch (NumberFormatException ignored) {
    }
  }

  public static void setSettingLAF() throws UnsupportedLookAndFeelException {
    UIManager.setLookAndFeel(Setting.DEFAULT_THEME.getEnumValue(Theme.class).getLookAndFeel());
  }

  private static void openLogIn() {
    ((Frame) TabbedPaneModel.getMainPanel().getContainer())
        .setTitle(Setting.STORE_NAME.getStringValue() + " (Login)");
    new SimpleLogInController().openTab();
  }
}
