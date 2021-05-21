package kernbeisser.Windows.MVC;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;

public class ActiveSessionModel <T extends Controller<? extends IView<T>, ? extends IModel<T>>> implements IModel<T>{

  protected final EntityManager em = DBConnection.getEntityManager();
  protected EntityTransaction et = em.getTransaction();

  protected ActiveSessionModel(){
    et.begin();
    System.out.println("starting session");
  }

  protected void commitTransactionAndStartNew(){
    em.flush();
    et.commit();
    et = em.getTransaction();
    et.begin();
  }


  @Override
  public void viewClosed() {
    em.flush();
    et.commit();
    em.close();
    System.out.println("closing session");
  }
}
