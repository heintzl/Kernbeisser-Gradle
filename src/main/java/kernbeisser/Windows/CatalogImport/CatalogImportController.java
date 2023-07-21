package kernbeisser.Windows.CatalogImport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Exeptions.UnknownFileFormatException;
import kernbeisser.Security.Key;
import kernbeisser.Tasks.Catalog.CatalogImporter;
import kernbeisser.Windows.MVC.Controller;

public class CatalogImportController extends Controller<CatalogImportView, CatalogImportModel> {

  @Key(PermissionKey.ACTION_OPEN_CATALOG_IMPORT)
  public CatalogImportController() throws PermissionKeyRequiredException {
    super(new CatalogImportModel());
  }

  @Override
  public void fillView(CatalogImportView catalogImportView) {}

  public void readFile(String path) {
    CatalogImportView view = getView();
    Path filePath = Paths.get(path);
    if (path.isEmpty() || !Files.exists(filePath)) {
      view.messagePathNotFound(path);
      return;
    }
    try {
      view.setReadErrors(model.readCatalog(filePath));
      CatalogImporter catalogImporter = model.getCatalogImporter();
      view.setScope(catalogImporter.getScope());
      view.setDescription(catalogImporter.getDescription());
      view.setCreatedDate(catalogImporter.getCreatedDate());
      view.setCreatedTime(catalogImporter.getCreatedTime());
      view.setValidFrom(catalogImporter.getValidFrom());
      view.setValidTo(catalogImporter.getValidTo());
    } catch (UnknownFileFormatException e) {
      view.messageFormatError(e.getMessage());
    }
  }

  public void applyChanges() {
    model.applyChanges();
  }
}
