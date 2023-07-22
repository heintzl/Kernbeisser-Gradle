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
      CatalogDataSource source, CatalogDataSource target, List<CatalogImportError> importErrors)
      throws CatalogImportErrorException, CatalogImportWarningException {
    boolean exists = true;
    String sourceUpdateType = source.getAenderungskennung();
    String targetUpdateType = "ZZ";
    Instant sourceUpdateDate = source.getAenderungsDatum();
    Instant targetUpdateDate = Instant.MIN;
    String sourceDesignation = source.getBezeichnung();
    exists = target != null;
    if (exists) {
      if (source.equals(target)) {
        return false;
      }
      targetUpdateType = target.getAenderungskennung();
      targetUpdateDate = target.getAenderungsDatum();
    }
    if ("X;V".contains(sourceUpdateType)) {
      if (!exists) {
        throw new CatalogImportErrorException(
            "Der Artikel wurde übersprungen, weil er nicht mehr gelistet ist");
      }
      if (!sourceUpdateType.equals(targetUpdateType)) {
        throw new CatalogImportWarningException(
            String.format(
                "Der Artikel \"%1s\" ist %2s nicht mehr gelistet",
                sourceDesignation, (sourceUpdateType.equals("V") ? "vorübergehend " : "")));
      }
    }
    Instant sourceActionValidTo = source.getAktionspreisGueltigBis();
    if (sourceActionValidTo != null && sourceActionValidTo.isBefore(Instant.now())) {
      throw new CatalogImportErrorException(
          "\"Der Aktions-Artikel wurde übersprungen, weil das Angebot bereits abgelaufen ist");
    }
    String sourceArticleNo = source.getArtikelNr();
    if (sourceArticleNo == null) {
      throw new CatalogImportErrorException(
          String.format(
              "Der Artikel wurde übersprungen, weil er keine Artikelnummer hat",
              sourceDesignation));
    }
    if (sourceArticleNo.length() == 6 && sourceArticleNo.startsWith("7")) {
      throw new CatalogImportErrorException(
          String.format(
              "Der Artikel \"%1s\" wurde übersprungen, weil es sich um Frischware handelt",
              sourceDesignation));
    }
    if (!targetUpdateDate.isBefore(sourceUpdateDate)) {
      throw new CatalogImportErrorException(
          "Der Artikel wurde übersprungen, weil er nicht aktueller ist, als der vorhandene");
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
