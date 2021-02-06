package kernbeisser.Forms.FormImplemetations.Supplier;

import javax.swing.*;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;
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
  public PermissionKey[] addPermission() {
    return new PermissionKey[] {PermissionKey.ADD_SUPPLIER};
  }

  @Override
  public PermissionKey[] editPermission() {
    return new PermissionKey[] {PermissionKey.EDIT_SUPPLIER};
  }

  @Override
  public PermissionKey[] removePermission() {
    return new PermissionKey[0];
  }

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
