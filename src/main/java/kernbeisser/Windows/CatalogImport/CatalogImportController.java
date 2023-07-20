package kernbeisser.Windows.CatalogImport;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.MVC.Controller;

public class CatalogImportController extends Controller<CatalogImportView, CatalogImportModel> {
  @Key(PermissionKey.ACTION_OPEN_CATALOG_IMPORT)
  public CatalogImportController(CatalogImportModel model) throws PermissionKeyRequiredException {
    super(model);
  }

  @Override
  public void fillView(CatalogImportView catalogImportView) {}
}
