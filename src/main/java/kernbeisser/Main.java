package kernbeisser;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Config;
import kernbeisser.DBEntitys.Job;
import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.Permission;
import kernbeisser.StartUp.StartUp;
import kernbeisser.Useful.BackGroundWorker;
import kernbeisser.Useful.Images;
import kernbeisser.Windows.LogIn.LogInView;
import kernbeisser.Windows.UserMenu.UserMenuView;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.function.Function;

public class Main {
    /**
     * sets the Look and Feel to Windows standard,
     * sets the Image path,
     * checks all needed Tables and PriceLists
     * and as least shows the LogIn Window
     */
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Images.setPath(new File("src/main/resources/Images"));
        EntityManager em = DBConnection.getEntityManager();
        Config.loadConfigs();
        if(Config.getConfig("firstStart")==null){
            new StartUp().waitFor();
            Config.setConfig("firstStart", LocalDate.now().toString());
        }
        BackGroundWorker.start();
        User user;
        try {
            user = em.createQuery("select u from User u", User.class).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            //TODO
            user = new User();
            user.setPermission(Permission.ADMIN);
        }
        User finalUser = user;
        SwingUtilities.invokeLater(() -> new UserMenuView(new LogInView(null),finalUser));
    }
    private static void createTestJobs(int count){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        for (int i = 0; i < count; i++) {
            Job j = new Job();
            j.setDescription("Test Description: "+i);
            j.setName("Test Job: "+i);
            em.persist(j);
        }
        em.flush();
        et.commit();
        em.close();
    }
    private static void printClass(Class c,Function<Field,String> transformer){
        for (Field field : c.getDeclaredFields()) {
            System.out.println(transformer.apply(field));
        }
    }
}
