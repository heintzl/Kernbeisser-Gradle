package kernbeisser;


import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.ConfigManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.StartUp.DataImport.DataImportController;
import kernbeisser.Windows.TabbedPanel.Tab;
import kernbeisser.Windows.TabbedPanel.TabbedPaneController;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.Window;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import java.lang.reflect.Field;
import java.util.function.Function;

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
        if (!ConfigManager.isDbInitialized()) {
            SwingUtilities.invokeLater(() -> new DataImportController().openAsWindow(new SimpleLogInController().openTab("Log In"),JFrameWindow::new));
        } else {
            openLogIn();
        }
    }

    public static void buildEnvironment()
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        UIManager.setLookAndFeel(Setting.DEFAULT_THEME.getEnumValue(Theme.class).getLookAndFeel());
        IconFontSwing.register(FontAwesome.getIconFont());
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
