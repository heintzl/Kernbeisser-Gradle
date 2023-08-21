package kernbeisser.Windows.CatalogImport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Exeptions.UnknownFileFormatException;
import kernbeisser.Security.Key;
import kernbeisser.Tasks.Catalog.CatalogImportError;
import kernbeisser.Tasks.Catalog.CatalogImporter;
import kernbeisser.Windows.MVC.Controller;

public class CatalogImportController extends Controller<CatalogImportView, CatalogImportModel> {

  @Key(PermissionKey.ACTION_OPEN_CATALOG_IMPORT)
  public CatalogImportController() throws PermissionKeyRequiredException {
    super(new CatalogImportModel());
  }

  @Override
  public void fillView(CatalogImportView catalogImportView) {
    setLastCatalogInfo();
  }

  public void setLastCatalogInfo() {
    getView()
        .setLastCatalogInfo(model.getLastCatalogCreationDate(), model.getLastCatalogValidDate());
  }

  public void readFile(String path) {
    CatalogImportView view = getView();
    Path filePath = Paths.get(path);
    if (path.isEmpty() || !Files.exists(filePath)) {
      view.messagePathNotFound(path);
      return;
    }
    view.indicateLoading(true);
    new Thread(
            () -> {
              try {
                List<CatalogImportError> readErrors = model.readCatalog(filePath);
                if (readErrors.size() == 0) {
                  readErrors.add(
                      new CatalogImportError(0, new Exception("Es gibt keine Fehlermeldungen")));
                }
                view.setReadErrors(readErrors);
                CatalogImporter catalogImporter = model.getCatalogImporter();
                view.setScope(catalogImporter.getScopeDescription());
                view.setDescription(catalogImporter.getDescription());
                view.setCreatedDate(catalogImporter.getCreatedDate());
                view.setCreatedTime(catalogImporter.getCreatedTime());
                view.setValidFrom(catalogImporter.getValidFrom());
                view.setValidTo(catalogImporter.getValidTo());
                view.setApplyChangesEnabled(true);
              } catch (UnknownFileFormatException e) {
                view.messageFormatError(e.getMessage());
                view.setApplyChangesEnabled(false);
              } finally {
                view.indicateLoading(false);
              }
            })
        .start();
  }

  public void applyChanges() {
    CatalogImportView view = getView();
    if (Instant.now().isAfter(model.getCatalogImporter().getValidTo())) {
      if (!view.confirmImportInValidCatalog("Die Gültigkeit des Katalogs ist bereits abgelaufen."))
        return;
    }
    if (!model.getLastCatalogCreationDate().isBefore(model.getCatalogImporter().getCreatedDate())) {
      if (!view.confirmImportInValidCatalog(
          "Der Katalog ist nicht aktueller, als der bereits vorhandene.")) return;
    }
    if (model.isCompleteCatalog()) {
      if (!view.confirmMergeCatalog()) {
        model.clearCatalog();
      }
      ;
    }
    new Thread(
            () -> {
              view.indicateLoading(true);
              List<CatalogImportError> importErrors = model.applyChanges();
              if (importErrors.isEmpty()) {
                importErrors.add(
                    new CatalogImportError(
                        0, new Exception("Es gibt keine Fehler oder Warnungen")));
              }
              view.setReadErrors(importErrors);
              model.refreshLastCatalogInfo();
              setLastCatalogInfo();
              view.indicateLoading(false);
            })
        .start();
  }
}
