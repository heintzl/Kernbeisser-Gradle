package kernbeisser;


import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.ConfigManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.Security.Proxy;
import kernbeisser.StartUp.DataImport.DataImportController;
import kernbeisser.Tasks.Catalog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import java.io.IOException;

public class Main {


    public static Logger logger = LogManager.getLogger(Main.class);
    /**
     * sets the Look and Feel to Windows standard,
     * sets the Image path,
     * checks all needed Tables and PriceLists
     * and as least shows the LogIn Window
     */
    public static void main(String[] args)
            throws UnsupportedLookAndFeelException {
        buildEnvironment();
        checkVersion();
        checkCatalog();
        if (!Setting.DB_INITIALIZED.getBooleanValue()) {
            SwingUtilities.invokeLater(() -> new DataImportController().openTab("Daten importieren"));
        } else {
            openLogIn();
        }
    }
    public static void checkCatalog() {

        logger.info("Checking Catalog ...");
        if (Setting.UPDATE_CATALOG_FROM_INTERNET.getBooleanValue()) {
            try {
                String info = Catalog.getInfoLineFromWeb();
                if(!Setting.INFO_LINE_LAST_CATALOG.getStringValue().equals(info)){
                    logger.info("Refreshing Catalog ...");
                    Catalog.updateCatalogFromWeb();
                    Setting.INFO_LINE_LAST_CATALOG.setValue(info);
                }
            } catch (IOException e) {
                logger.error("Cannot build connection to web skipping Catalog refreshing");
                Tools.showUnexpectedErrorWarning(e);
            }
        } else {
            String infoLine = ConfigManager.getCatalogInfoLine();
            if (infoLine == null){
                logger.error("Cannot find Catalog File skipping Catalog refreshing");
                return;
            }
            if (!infoLine.equals(Setting.INFO_LINE_LAST_CATALOG.getStringValue())) {
                logger.info("Refreshing Catalog ...");
                Catalog.updateCatalogFromKornKraftDefault(ConfigManager.getCatalogFile());
                Setting.INFO_LINE_LAST_CATALOG.setValue(infoLine);
            }
        }
        logger.info("Catalog up to Date!");
    }

    public static void checkVersion(){
        logger.info("Aktuelle DB Version: "+Setting.DB_VERSION.getStringValue()+" | Branch Version: "+Setting.DB_VERSION.getDefaultValue());
        if (!Setting.DB_VERSION.getStringValue().equals(Setting.DB_VERSION.getDefaultValue())&&JOptionPane.showConfirmDialog(null,
                                                                                                                             "Ihre Datenbankversion entspricht nicht der aktuellsten Version.\nAktuelle Version: "+
                                                                                                                             Setting.DB_VERSION.getStringValue()+"\nNeuste Verstion: "+Setting.DB_VERSION.getDefaultValue()+ "\nWollen sie die Datenbank leeren und eine neue Datenbank instanz\nerstellen?"
        )==0) updateDBVersion();

    }

    public static void updateDBVersion(){
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
}
