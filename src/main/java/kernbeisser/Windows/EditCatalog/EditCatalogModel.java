package kernbeisser.Windows.EditCatalog;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class EditCatalogModel implements IModel<EditCatalogController> {

  @Getter private final Collection<CatalogEntry> catalog;

  public EditCatalogModel() {
    catalog = getQuery();
  }

  private static List<CatalogEntry> getQuery() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<CatalogEntry> cr = cb.createQuery(CatalogEntry.class);
    cr.select(cr.from(CatalogEntry.class));
    TypedQuery<CatalogEntry> q = em.createQuery(cr);
    return q.getResultList();
  }

  // Ignores the Max rows setting
  Collection<CatalogEntry> searchable(
      String s, int max, Predicate<CatalogEntry> catalogEntryFilter) {
    return catalog.stream()
        .filter(e -> e.matches(s))
        .filter(catalogEntryFilter)
        .collect(Collectors.toList());
  }
}
