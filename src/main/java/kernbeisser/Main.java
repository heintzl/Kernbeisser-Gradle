package kernbeisser;


import kernbeisser.CustomComponents.ObjectTree.ChildFactory;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Config;
import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.Permission;
import kernbeisser.StartUp.StartUp;
import kernbeisser.Useful.BackGroundWorker;
import kernbeisser.Useful.Images;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInView;
import kernbeisser.Windows.Pay.Pay;
import kernbeisser.Windows.UserMenu.UserMenu;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
        SwingUtilities.invokeLater(() -> new UserMenu(new LogInView(null),finalUser));
    }
}
