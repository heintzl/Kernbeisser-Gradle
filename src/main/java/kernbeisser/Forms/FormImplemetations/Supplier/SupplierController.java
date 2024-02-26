package kernbeisser.Forms.FormImplemetations.Supplier;

import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.Exceptions.SilentParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import rs.groump.PermissionKey;

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

  public void confirmSurcharge(Supplier supplier, Mode mode) throws SilentParseException {
    if (supplier.getDefaultSurcharge() < 0.00 || supplier.getDefaultSurcharge() > 1.) {
      getView().messageSurchargeNotValid();
      throw new SilentParseException();
    }

    if (supplier.getDefaultSurcharge() == 0.0) {
      if (!getView().messageConfirmSurcharge(supplier.getDefaultSurcharge())) {
        throw new SilentParseException();
      }
    }
  }

  @Override
  public void remove(Supplier supplier) {
    if (supplier == null) {
      getView().messageSelectSupplierFirst();
      return;
    }

    try {
      Tools.delete(supplier);
    } catch (RuntimeException e) {
      try {
        throw e.getCause();
      } catch (ConstraintViolationException cve) {
        getView().messageConstraintViolation();
      } catch (Throwable throwable) {
        Tools.showUnexpectedErrorWarning(e);
      }
    }
  }
}
