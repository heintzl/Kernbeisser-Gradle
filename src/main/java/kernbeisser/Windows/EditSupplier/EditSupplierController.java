package kernbeisser.Windows.EditSupplier;

import javax.persistence.PersistenceException;
import javax.swing.*;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;

public class EditSupplierController extends Controller<EditSupplierView, EditSupplierModel> {

  public EditSupplierController(Supplier supplier, Mode mode) {
    super(new EditSupplierModel(supplier, mode));
    if (mode.equals(Mode.REMOVE)) {
      try {
        Tools.delete(supplier);
      } catch (PersistenceException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
          JOptionPane.showMessageDialog(
              null,
              "Der Lieferant kann nicht gelöscht werden, da dieser noch auf andere Objekte verweisst");
        }
      }
    }
  }

  @NotNull
  @Override
  public EditSupplierModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditSupplierView editSupplierView) {
    var view = getView();
    view.getObjectForm().setSource(getModel().getSupplier());
  }



  public void commit() {
    var view = getView();
    if (view.getObjectForm().applyMode(model.getMode())) {
      view.back();
    }
  }

  String validateName(String name) throws CannotParseException {
    switch (model.getMode()) {
      case EDIT:
        if (name.equals(model.getSupplier().getName())) return name;
      case ADD:
        if (model.nameExists(name)) {
          var view = getView();
          view.nameAlreadyExists();
          throw new CannotParseException("short name is already taken");
        } else return name;
      default:
        throw new UnsupportedOperationException(model.getMode() + " is not supported");
    }
  }

  String validateShortName(String name) throws CannotParseException {
    var view = getView();
    switch (model.getMode()) {
      case EDIT:
        if (name.equals(model.getSupplier().getShortName())) return name;
      case ADD:
        if (model.shortNameExists(name)) {
          view.shortNameAlreadyExists();
          throw new CannotParseException("short name is already taken");
        } else return name;
      default:
        throw new UnsupportedOperationException(model.getMode() + " is not supported");
    }
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    switch (getModel().getMode()){
      case ADD:
        return new PermissionKey[]{PermissionKey.ADD_SUPPLIER};
      case EDIT:
        return new PermissionKey[]{PermissionKey.EDIT_SUPPLIER};
      case REMOVE:
        return new PermissionKey[]{PermissionKey.REMOVE_SUPPLIER};
    }
    throw new UnsupportedOperationException("undefined mode");
  }
}
