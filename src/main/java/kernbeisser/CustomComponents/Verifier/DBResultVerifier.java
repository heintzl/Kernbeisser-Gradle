package kernbeisser.CustomComponents.Verifier;

import kernbeisser.DBConnection.DBConnection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public abstract class DBResultVerifier extends TextComponentVerifier {

    abstract boolean allowAlreadyExists();

    @Override
    public boolean verify(JTextComponent component) {
        EntityManager em = DBConnection.getEntityManager();
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
