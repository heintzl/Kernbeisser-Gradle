package kernbeisser.Windows.ManageUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.User;
import kernbeisser.DBEntitys.UserGroup;
import kernbeisser.Enums.Permission;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import org.hibernate.HibernateError;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

public class ManageUserController implements Controller {
    private static final int MIN_USERNAME_LENGTH = 5;
    public static final int SUCCESS = 0;
    public static final int USERNAME_TO_SHORT = 1;
    public static final int USERNAME_ALREADY_EXISTS = 2;
    public static final int CANNOT_LEAF_USER_GROUP = 3;
    public static final int NO_USER_GROUP_SELECTED = 4;
    private ManageUserView view;
    private ManageUserModel model;
    ManageUserController(ManageUserView view, Permission permission){
        this.view=view;
        this.model=new ManageUserModel(permission);
        view.setUsers(model.getAllUser());
    }

    @Override
    public void refresh() {

    }

    boolean usernameToShort(User user){
        return user.getUsername().length()<MIN_USERNAME_LENGTH;
    }

    int addUser(){
        if((!view.isAlone())&&model.getUserGroup()==null){
            return NO_USER_GROUP_SELECTED;
        }
        User newUser = new User();
        view.collectData(newUser);
        if(usernameToShort(newUser)){
            return USERNAME_TO_SHORT;
        }
        newUser.setUserGroup(model.getUserGroup());
        newUser.getJobs().clear();
        newUser.getJobs().addAll(model.getSelectedJobs());
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        if(view.isAlone()){
            UserGroup alone = new UserGroup();
            em.persist(alone);
            newUser.setUserGroup(alone);
        }
        try{
            em.persist(newUser);
        }catch (PersistenceException e){
            System.err.println(e.getMessage());
            et.rollback();
            em.close();
            return USERNAME_ALREADY_EXISTS;
        }
        em.flush();
        et.commit();
        em.close();
        return SUCCESS;
    }

    int editUser(){
        if((!view.isAlone())&&model.getUserGroup()==null){
            return NO_USER_GROUP_SELECTED;
        }
        User oldContent = view.getSelectedUser();
        String oldPassword = oldContent.getPassword();
        User newContent = view.collectData(oldContent);
        if(newContent.getPassword().length()==0)
            newContent.setPassword(encrypt(oldPassword));
        if(usernameToShort(newContent)){
            return USERNAME_TO_SHORT;
        }
        newContent.setUserGroup(model.getUserGroup());
        newContent.getJobs().clear();
        newContent.getJobs().addAll(model.getSelectedJobs());
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        if(view.isAlone()){
            if(oldContent.getUserGroup().getMembers().size()<1){
                et.rollback();
                em.close();
                return CANNOT_LEAF_USER_GROUP;
            }
            UserGroup alone = new UserGroup();
            em.persist(alone);
            newContent.setUserGroup(alone);
        }
        try{
            em.persist(newContent);
        }catch (PersistenceException e){
            System.err.println(e.getMessage());
            et.rollback();
            em.close();
            return USERNAME_ALREADY_EXISTS;
        }
        em.flush();
        et.commit();
        em.close();
        return SUCCESS;
    }

    void loadUser(User user){
        model.setUserGroup(user.getUserGroup());
        model.setSelectedJobs(user.getJobs());
        view.paste(user);
    }

    String encrypt(String password){
        return BCrypt.withDefaults().hashToString(12,password.toCharArray());
    }

    void refreshUserList(){
        view.setUsers(model.getAllUser());
    }

    void requestJobSelector(){
        view.openJobSelector(model.getSelectedJobs());
    }

    void selectUserGroup(UserGroup userGroup){
        model.setUserGroup(userGroup);
    }

    @Override
    public ManageUserView getView() {
        return view;
    }

    @Override
    public ManageUserModel getModel() {
        return model;
    }

}
