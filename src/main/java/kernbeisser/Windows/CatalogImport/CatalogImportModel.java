package kernbeisser.Windows.CatalogImport;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.CatalogDataSource;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.CatalogImportErrorException;
import kernbeisser.Exeptions.CatalogImportWarningException;
import kernbeisser.Exeptions.UnknownFileFormatException;
import kernbeisser.Tasks.Catalog.CatalogImportError;
import kernbeisser.Tasks.Catalog.CatalogImporter;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class CatalogImportModel implements IModel<CatalogImportController> {
  @Getter private CatalogImporter catalogImporter;
  @Getter private Instant lastCatalogCreationDate;
  @Getter private Instant lastCatalogValidDate;

  public CatalogImportModel() {
    refreshLastCatalogInfo();
  }

  public List<CatalogImportError> readCatalog(Path bnnFile) throws UnknownFileFormatException {
    catalogImporter = new CatalogImporter(bnnFile);
    return catalogImporter.getReadErrors();
  }

  public void refreshLastCatalogInfo() {
    String[] lastCataloginfo =
        Setting.INFO_LINE_LAST_CATALOG.getStringValue().split(CatalogImporter.DELIMITER);
    if (lastCataloginfo.length > 8) {
      lastCatalogCreationDate =
          Date.parseInstantDate(lastCataloginfo[9], Date.INSTANT_CATALOG_DATE);
      lastCatalogValidDate = Date.parseInstantDate(lastCataloginfo[8], Date.INSTANT_CATALOG_DATE);
    }
  }

  private boolean checkImport(
      CatalogDataSource source, CatalogDataSource existing, List<CatalogImportError> importErrors)
      throws CatalogImportErrorException, CatalogImportWarningException {
    boolean exists = true;
    String updateTypeSource = source.getAenderungskennung();
    String updateTypeExisting = "ZZ";
    if (existing == null) {
      exists = false;
    } else {
      if (source.equals(existing)) {
        return false;
      }
      updateTypeExisting = existing.getAenderungskennung();
    }
    if ("X;V".contains(updateTypeSource)) {
      if (!exists) {
        throw new CatalogImportErrorException(
            "Der Artikel wurde übersprungen, weil er nicht mehr gelistet ist");
      }
      if (!updateTypeSource.equals(updateTypeExisting)) {
        throw new CatalogImportWarningException(
            "Der Artikel ist "
                + (updateTypeSource.equals("V") ? "vorübergehend " : "")
                + "nicht mehr gelistet");
      }
    }
    Instant actionValidToSource = source.getAktionspreisGueltigBis();
    if (actionValidToSource != null && actionValidToSource.isBefore(Instant.now())) {
      throw new CatalogImportErrorException(
          "\"Der Aktions-Artikel wurde übersprungen, weil das Angebot bereits abgelaufen ist");
    }
    return true;
  }

  public List<CatalogImportError> applyChanges() {
    Map<String, CatalogDataSource> existingCatalog = new HashMap<>();
    if (catalogImporter.getScope().equals("V")) {
      CatalogDataSource.clearCatalog();
    } else {
      CatalogDataSource.getCatalog().forEach(e -> existingCatalog.put(e.getUXString(), e));
    }
    List<CatalogImportError> importErrors = new ArrayList<>();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (CatalogDataSource source : catalogImporter.getCatalog()) {
      CatalogDataSource existing = existingCatalog.get(source.getUXString());
      if (existing != null) {
        source.setId(existing.getId());
      }
      try {
        if (checkImport(source, existing, importErrors)) {
          em.merge(source);
        }
      } catch (CatalogImportWarningException | CatalogImportErrorException e) {
        importErrors.add(new CatalogImportError(source.getArtikelNrInt(), e));
        if (e.getClass().equals(CatalogImportWarningException.class)) {
          em.merge(source);
        }
      }
    }
    Setting.INFO_LINE_LAST_CATALOG.changeValue(catalogImporter.getInfoLine());
    refreshLastCatalogInfo();
    return importErrors;
  }
}
