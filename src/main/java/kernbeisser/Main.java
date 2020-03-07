package kernbeisser;


import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.ConfigManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.StartUp.DataImport.DataImportController;
import kernbeisser.StartUp.DataImport.DataImportView;
import kernbeisser.Windows.LogIn.LogInController;
import kernbeisser.Windows.LogIn.LogInView;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
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
                   IllegalAccessException, IOException, URISyntaxException {
        buildEnvironment();
        if (!ConfigManager.getHeader().getBoolean("dbIsInitialized")) {
            SwingUtilities.invokeLater(() -> new DataImportController(new LogInController(null).getView()));
        } else {
            openLogIn();
        }
    }

    public static void buildEnvironment()
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        IconFontSwing.register(FontAwesome.getIconFont());
        DBConnection.getEntityManager();
    }

    private static void openLogIn() {
        SwingUtilities.invokeLater(() -> new LogInController(null));
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

    public static void makeAdmin(User user) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        User us = em.find(user.getClass(), user.getId());
        us.getPermissions()
          .add(em.createQuery("select p from Permission p where name like 'Admin'", Permission.class)
                 .getSingleResult());
        em.persist(us);
        em.flush();
        et.commit();
        em.close();
    }

    private static void printClass(Class c, Function<Field,String> transformer) {
        for (Field field : c.getDeclaredFields()) {
            System.out.println(transformer.apply(field));
        }
    }

}
