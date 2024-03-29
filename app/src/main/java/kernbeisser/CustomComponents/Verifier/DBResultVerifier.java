package kernbeisser.CustomComponents.Verifier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import kernbeisser.DBConnection.DBConnection;
import lombok.Cleanup;

public abstract class DBResultVerifier extends TextComponentVerifier {

  abstract boolean allowAlreadyExists();

  @Override
  public boolean verify(JTextComponent component) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      em.createQuery(getQuery()).setParameter("s", component.getText()).getSingleResult();
      em.close();
      return allowAlreadyExists();
    } catch (NoResultException e) {
      em.close();
      return !allowAlreadyExists();
    }
  }

  @Override
  public boolean shouldYieldFocus(JComponent input) {
    boolean verified = verify(input);
    if (!verified) {
      JOptionPane.showMessageDialog(input, unexpectedResult());
    }
    return verified;
  }

  public abstract String getQuery();

  public abstract String unexpectedResult();
}
