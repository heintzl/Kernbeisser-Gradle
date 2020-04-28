package kernbeisser;


import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.StartUp.DataImport.DataImportController;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;

public class Main {



    /**
     * sets the Look and Feel to Windows standard,
     * sets the Image path,
     * checks all needed Tables and PriceLists
     * and as least shows the LogIn Window
     */
    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        buildEnvironment();
        checkVersion();
        if (!Setting.DB_INITIALIZED.getBooleanValue()) {
            SwingUtilities.invokeLater(() -> new DataImportController().openAsWindow(new SimpleLogInController().openTab("Log In"),JFrameWindow::new));
        } else {
            openLogIn();
        }
    }

    public static void checkVersion(){
        if (!Setting.DB_VERSION.getStringValue().equals(Setting.DB_VERSION.getDefaultValue())&&JOptionPane.showConfirmDialog(null,
                                                                                                                             "Ihre Datenbankversion entspricht nicht der aktuellsten Version.\nAktuelle Version: "+
                                                                                                                             Setting.DB_VERSION.getStringValue()+"\nNeuste Verstion: "+Setting.DB_VERSION.getDefaultValue()+ "\nWollen sie die Datenbank leeren und eine neue Datenbank instanz\nerstellen?"
        )==0) updateDBVersion();

    }

    public static void updateDBVersion(){
        DBConnection.updateDatabase();
    }

    public static void buildEnvironment() throws UnsupportedLookAndFeelException {
        setSettingLAF();
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
