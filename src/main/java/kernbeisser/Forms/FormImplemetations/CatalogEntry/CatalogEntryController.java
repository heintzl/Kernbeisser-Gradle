package kernbeisser.Forms.FormImplemetations.CatalogEntry;

import java.util.function.Supplier;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessDeniedException;

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

  // currently read only!
  @Override
  public void addPermission() {
    throw new AccessDeniedException();
  }

  @Override
  public void editPermission() {
    throw new AccessDeniedException();
  }

  @Override
  public void removePermission() {
    throw new AccessDeniedException();
  }

  @Override
  public ObjectForm<CatalogEntry> getObjectContainer() {
    return getView().getForm();
  }

  @Override
  public Supplier<CatalogEntry> defaultFactory() {
    return CatalogEntry::new;
  }
}
