package kernbeisser.Windows.ManageUser;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.User;
import kernbeisser.DBEntitys.UserGroup;
import kernbeisser.Enums.UserPersistFeedback;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.UserUI.UserUIView;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

public class ManageUserUIController implements Controller {
    private ManageUserUIView view;
    private ManageUserUIModel model;
    ManageUserUIController(ManageUserUIView view){
        this.view=view;
        this.model=new ManageUserUIModel();
        view.setUsers(model.getAllUser());
    }

    @Override
    public ManageUserUIModel getModel() {
        return model;
    }

    @Override
    public void refresh() {

    }

    @Override
    public ManageUserUIView getView() {
        return view;
    }

    public void add(){
        UserUIView userUIView = new UserUIView(view,e -> {
            EntityManager em = DBConnection.getEntityManager();
            EntityTransaction et = em.getTransaction();
            User newUser = new User();
            newUser.paste(e);
            et.begin();
            try {
                UserGroup userGroup = new UserGroup();
                em.persist(userGroup);
                newUser.setUserGroup(userGroup);
                em.persist(newUser);
            }catch (PersistenceException ex){
                et.rollback();
                em.close();
                return extractException(ex);
            }
            em.flush();
            et.commit();
            em.close();
            refreshUserTable();
            return UserPersistFeedback.SUCCESS;
        },view::applyFeedback);
        User selected = view.getSelectedUser();
        if(selected!=null)
            userUIView.loadUser(view.getSelectedUser());
    }

    public void remove(){
        User selected = view.getSelectedUser();
        if(selected.getUserGroup().getMembers().size()<1){
            view.applyFeedback(UserPersistFeedback.CANNOT_LEAF_USER_GROUP);
        }
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.remove(em.find(User.class,selected.getId()));
        et.commit();
        em.close();
        refreshUserTable();
        view.applyFeedback(UserPersistFeedback.SUCCESS);
    }

    public void edit(){
        UserUIView userUIView = new UserUIView(view,e -> {
            EntityManager em = DBConnection.getEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();
            User user = em.find(User.class,e.getId());
            user.paste(e);
            try {
                em.persist(user);
            }catch (PersistenceException ex){
                et.rollback();
                em.close();
                return extractException(ex);
            }
            em.flush();
            et.commit();
            em.close();
            refreshUserTable();
            return UserPersistFeedback.SUCCESS;
        },view::applyFeedback);
        User selected = view.getSelectedUser();
        if(selected!=null)
            userUIView.loadUser(view.getSelectedUser());
    }

    private UserPersistFeedback extractException(PersistenceException ex){
        if (ex.getCause() instanceof PropertyValueException) {
            return UserPersistFeedback.UN_COMPLETE_USER;
        }else if(ex.getCause() instanceof ConstraintViolationException){
            return UserPersistFeedback.USERNAME_ALREADY_EXISTS;
        }
        ex.printStackTrace();
        return UserPersistFeedback.ERROR;
    }

    private void refreshUserTable(){
        view.setUsers(model.getAllUser());
    }
}
