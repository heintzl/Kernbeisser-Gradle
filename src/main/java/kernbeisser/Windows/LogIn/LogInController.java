package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.UserMenu.UserMenu;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class LogInController implements Controller {
    private LogInView view;
    private LogInModel model;

    LogInController(LogInView view){
        this.view=view;
        this.model=new LogInModel();
        model.setLoggedIn(null);
    }

    static final int INCORRECT_USERNAME = 0;
    static final int INCORRECT_PASSWORD = 1;
    static final int SUCCESS = 2;

    int logIn(String username, char[] password) {
        EntityManager em = DBConnection.getEntityManager();
        try{
            User user = em.createQuery(
                    "select u from User u where u.username like :username", User.class)
                    .setParameter("username", username).
                            getSingleResult();
            if(!(BCrypt.verifyer().verify(password,user.getPassword().toCharArray()).verified)){
                model.setLoggedIn(user);
                return SUCCESS;
            }else {
                return INCORRECT_PASSWORD;
            }
        }catch (NoResultException e){
            return INCORRECT_USERNAME;
        }
    }

    List<List<User>> getABCUser(){
        EntityManager em = DBConnection.getEntityManager();
        List<List<User>> out = new ArrayList<>();
        for (int i = 97; i < 123; i++) {
            out.add(em.createQuery("select u from User u where u.username like '" + ((char) i) + "%' Order by username asc",User.class).getResultList());
        }
        em.close();
        return out;
    }
    List<User> getAllWhichBeginsWith(char i){
        EntityManager em = DBConnection.getEntityManager();
        List<User> out = em.createQuery("select u from User u where u.username like '" +i+ "%' Order by username asc",User.class).getResultList();
        em.close();
        return out;
    }
    List<User> getAllUser(){
        return User.getAll(null);
    }
    void openUserMenu(){
        new UserMenu(view,model.getLoggedIn());
    }

    @Override
    public void refresh() {

    }

    @Override
    public LogInView getView() {
        return view;
    }

    @Override
    public LogInModel getModel() {
        return model;
    }
}
