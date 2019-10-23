package kernbeisser;


import kernbeisser.Windows.LogIn;
import kernbeisser.Windows.UserMenu;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;

public class Main {
    /**
     * sets the Look and Feel to Windows standard,
     * sets the Image path,
     * checks all needed Tables and PriceLists
     * and as least shows the LogIn Window
     * */
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Images.setPath(new File("src/main/resources/Images"));
        EntityManager em = DBConnection.getEntityManager();
        User user;
        try {
            user = em.createQuery("select u from User u", User.class).setMaxResults(1).getSingleResult();
        }catch (NoResultException e) {
            user = new User();
            user.setPermission(Permission.ADMIN);
        }
        User finalUser = user;
        SwingUtilities.invokeLater(() -> new UserMenu(finalUser) {
            @Override
            public void finish() {
                new LogIn();
                dispose();
            }
        }.setIconImage(Images.getImage("Icon.png")));
}
private static String big(String s) {
        return s.replaceFirst(s.charAt(0)+"",(s.charAt(0)+"").toUpperCase());
    }
}
