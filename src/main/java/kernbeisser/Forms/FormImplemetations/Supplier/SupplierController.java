package kernbeisser.Forms.FormImplemetations.Supplier;

import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Key;
import org.jetbrains.annotations.NotNull;

public class SupplierController extends FormController<SupplierView, SupplierModel, Supplier> {

  public SupplierController() {
    super(new SupplierModel());
  }

  @NotNull
  @Override
  public SupplierModel getModel() {
    return model;
  }

  @Override
  public void fillView(SupplierView supplierView) {}

  @Override
  @Key(PermissionKey.ADD_SUPPLIER)
  public void addPermission() {}

  @Override
  @Key(PermissionKey.EDIT_SUPPLIER)
  public void editPermission() {}

  @Override
  public void removePermission() {}

  @Override
  public ObjectForm<Supplier> getObjectContainer() {
    return getView().getObjectForm();
  }

  public boolean isShortNameUnique(String t) {
    return model.shortNameExists(t);
  }

  public boolean isNameUnique(String t) {
    return model.nameExists(t);
  }

  @Override
  public java.util.function.Supplier<Supplier> defaultFactory() {
    return Supplier::new;
  }
}
