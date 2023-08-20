package kernbeisser.Windows.EditCatalog;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class EditCatalogModel implements IModel<EditCatalogController> {

  @Getter private final Collection<CatalogEntry> catalog;

  public EditCatalogModel() {
    catalog = DBConnection.getAll(CatalogEntry.class);
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
