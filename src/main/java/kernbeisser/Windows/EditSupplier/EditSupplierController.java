package kernbeisser.Windows.EditSupplier;

import javax.persistence.PersistenceException;
import javax.swing.*;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;

public class EditSupplierController implements IController<EditSupplierView, EditSupplierModel> {

  private final EditSupplierModel model;
  private EditSupplierView view;

  public EditSupplierController(Supplier supplier, Mode mode) {
    model = new EditSupplierModel(supplier, mode);
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
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void commit() {
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
          view.nameAlreadyExists();
          throw new CannotParseException("short name is already taken");
        } else return name;
      default:
        throw new UnsupportedOperationException(model.getMode() + " is not supported");
    }
  }

  String validateShortName(String name) throws CannotParseException {
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
}
