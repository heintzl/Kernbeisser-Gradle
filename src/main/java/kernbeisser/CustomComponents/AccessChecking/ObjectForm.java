package kernbeisser.CustomComponents.AccessChecking;

import javax.swing.*;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;

public class ObjectForm<P> {
  private ObjectValidator<P> objectValidator;

  private final Bounded<P, ?>[] boundedFields;

  private boolean checkInputVerifier = true;

  private P original;
  private P accessModel;

  @SafeVarargs
  public ObjectForm(P original, Bounded<P, ?>... boundedFields) {
    this.boundedFields = boundedFields;
    setSource(original);
  }

  public void setSource(P data) {
    this.original = data;
    this.accessModel = Tools.clone(original);
    refreshAccess();
    setData(data);
  }

  public P getData() throws CannotParseException {
    return getData(false);
  }

  public P getData(boolean pingErrors) throws CannotParseException {
    boolean success = true;
    P originalCopy = Tools.clone(original);
    for (Bounded<P, ?> boundedField : boundedFields) {
      try {
        if ((boundedField.isInputChanged() || boundedField.canRead(accessModel))
            && boundedField.canWrite(accessModel)) {
          boundedField.writeInto(originalCopy);
        }
      } catch (CannotParseException e) {
        success = false;
        if (pingErrors) boundedField.markWrongInput();
      }
    }
    if (!success) throw new CannotParseException();
    return originalCopy;
  }

  public P getDataIgnoreWrongInput() {
    P originalCopy = Tools.clone(original);
    for (Bounded<P, ?> boundedField : boundedFields) {
      try {
        if ((boundedField.isInputChanged() || boundedField.canRead(accessModel))
            && boundedField.canWrite(accessModel)) {
          boundedField.writeInto(originalCopy);
        }
      } catch (CannotParseException ignored) {
      }
    }
    return originalCopy;
  }

  private boolean isValidInput(Bounded<P, ?> bounded) {
    return bounded.validInput()
        && (!checkInputVerifier
            || !(bounded instanceof JComponent)
            || ((JComponent) bounded).getInputVerifier() == null
            || ((JComponent) bounded).getInputVerifier().verify((JComponent) bounded));
  }

  private void setData(@NotNull P data) {
    for (Bounded<P, ?> boundedField : boundedFields) {
      boundedField.setObjectData(data);
    }
  }

  public void refreshAccess() {
    for (Bounded<P, ?> boundedField : boundedFields) {
      boundedField.setReadable(boundedField.canRead(accessModel));
      boundedField.setWriteable(boundedField.canWrite(accessModel));
    }
  }

  public void markErrors() {
    for (Bounded<P, ?> field : boundedFields) {
      if (!isValidInput(field)) {
        field.markWrongInput();
      }
    }
  }

  public boolean isValid() {
    boolean valid = true;
    for (Bounded<P, ?> field : boundedFields) {
      valid = valid && field.validInput();
    }
    return valid;
  }

  public boolean persistAsNewEntity() {
    try {
      P data = getData(true);
      try {
        objectValidator.validate(data);
      }catch (CannotParseException e){
        return false;
      }
      Tools.add(Proxy.removeProxy(data));
      JOptionPane.showMessageDialog(null, "Das Objeckt wurde erfolgreich persistiert");
      return true;
    } catch (CannotParseException e) {
      JOptionPane.showMessageDialog(null, "Die folgenden Felder wurden nicht korrekt ausgefüllt");
      return false;
    }
  }

  public boolean persistChanges() {
    try {
      P data = getData(true);
      try {
        objectValidator.validate(data);
      }catch (CannotParseException e){
        return false;
      }
      Tools.edit(Tools.getId(original), (Proxy.removeProxy(data)));
      return true;
    } catch (CannotParseException e) {
      JOptionPane.showMessageDialog(null, "Die folgenden Felder wurden nicht korrekt ausgefüllt");
      return false;
    }
  }

  public boolean applyMode(Mode mode) {
    switch (mode) {
      case REMOVE:
        Tools.delete(original.getClass(), Tools.getId(original));
        return true;
      case EDIT:
        return persistChanges();
      case ADD:
        return persistAsNewEntity();
      default:
        throw new UnsupportedOperationException(mode + " is not supported by applyMode");
    }
  }

  public boolean isCheckInputVerifier() {
    return checkInputVerifier;
  }

  public void setCheckInputVerifier(boolean checkInputVerifier) {
    this.checkInputVerifier = checkInputVerifier;
  }

  public ObjectValidator<P> getObjectValidator() {
    return objectValidator;
  }

  public void setObjectValidator(ObjectValidator<P> objectValidator) {
    this.objectValidator = objectValidator;
  }
}
