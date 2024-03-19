package kernbeisser.VersionIntegrationTools.UpdatingTools;

import static kernbeisser.DBEntities.Types.PreOrderField.delivery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

public class MigrateOpenPreOrders implements VersionUpdatingTool {
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<PreOrder> relevantPreorders =
        DBConnection.getConditioned(PreOrder.class, delivery.isNull()).stream()
            .filter(p -> p.getCatalogEntry() == null)
            .toList();
    for (PreOrder preOrder : relevantPreorders) {
      int kkNUmber = preOrder.getArticle().getSuppliersItemNumber();
      Optional<CatalogEntry> entry =
          DBConnection.getConditioned(
                  CatalogEntry.class,
                  new FieldIdentifier<>(CatalogEntry.class, "artikelNr")
                      .eq(Integer.toString(kkNUmber)))
              .stream()
              .filter(e -> !e.getAktionspreis())
              .findFirst();
      PreOrder existingPreOrder = em.find(PreOrder.class, preOrder.getId());
      if (entry.isPresent()) {
        existingPreOrder.setCatalogEntry(entry.get());
        em.merge(existingPreOrder);
      } else {
        Article lostArticle = preOrder.getArticle();
        JOptionPane.showMessageDialog(
            null,
            String.format(
                "Die Bestellung von %d mal %s "
                    + "(KB_Artikelnummer %d) für %s wird gelöscht, weil der Artikel nicht im Großhandelskatalog enthalten ist!",
                preOrder.getAmount(),
                lostArticle.getName(),
                lostArticle.getSuppliersItemNumber(),
                preOrder.getUser().getFullName(false)),
            "Fehler bei der Übernahme von Vorbestellungen",
            JOptionPane.ERROR_MESSAGE);
        em.remove(existingPreOrder);
      }
    }
  }
}
