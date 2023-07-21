package kernbeisser.Windows.CatalogImport;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.CatalogDataSource;
import kernbeisser.Exeptions.UnknownFileFormatException;
import kernbeisser.Tasks.Catalog.CatalogImportError;
import kernbeisser.Tasks.Catalog.CatalogImporter;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class CatalogImportModel implements IModel<CatalogImportController> {
  @Getter CatalogImporter catalogImporter;

  List<CatalogImportError> readCatalog(Path bnnFile) throws UnknownFileFormatException {
    catalogImporter = new CatalogImporter(bnnFile);
    return catalogImporter.getReadErrors();
  }

  public void applyChanges() {
    Map<String, Long> existingCatalog = new HashMap<>();
    CatalogDataSource.getCatalog().forEach(e -> existingCatalog.put(e.getArtikelNr(), e.getId()));
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (CatalogDataSource source : catalogImporter.getCatalog()) {
      Long id = existingCatalog.get(source.getArtikelNr());
      if (id != null) {
        source.setId(id);
      }
      em.merge(source);
    }
  }
}
