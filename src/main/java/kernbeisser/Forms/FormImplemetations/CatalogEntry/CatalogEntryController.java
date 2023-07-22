package kernbeisser.Forms.FormImplemetations.CatalogEntry;

import java.util.function.Supplier;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Key;
import org.jetbrains.annotations.NotNull;

public class CatalogEntryController
    extends FormController<CatalogEntryView, CatalogEntryModel, CatalogEntry> {

  public CatalogEntryController() {
    super(new CatalogEntryModel());
  }

  @NotNull
  @Override
  public CatalogEntryModel getModel() {
    return model;
  }

  @Override
  public void fillView(CatalogEntryView catalogEntryView) {}

  @Override
  @Key(PermissionKey.ADD_JOB)
  public void addPermission() {}

  @Override
  @Key(PermissionKey.EDIT_JOB)
  public void editPermission() {}

  @Override
  @Key(PermissionKey.REMOVE_JOB)
  public void removePermission() {}

  @Override
  public ObjectForm<CatalogEntry> getObjectContainer() {
    return getView().getForm();
  }

  @Override
  public Supplier<CatalogEntry> defaultFactory() {
    return CatalogEntry::new;
  }
}
