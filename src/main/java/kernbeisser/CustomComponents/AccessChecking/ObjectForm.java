package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ObjectForm<P> {
  private final Bounded<P, ?>[] boundedFields;

  private P original;
  private P accessModel;

  @SafeVarargs
  public ObjectForm(P original, Bounded<P, ?>... boundedFields) {
    this.boundedFields = boundedFields;
    setSource(original);
  }

  public P getOriginal() {
    return original;
  }

  public void pullData() {
    setData(original);
  }

  public void setSource(P data) {
    this.original = data;
    this.accessModel = Tools.clone(original);
    refreshAccess();
    setData(data);
  }

  public P getData() throws CannotParseException {
    P originalCopy = Tools.clone(original);
    for (Bounded<P, ?> boundedField : boundedFields) {
      try {
        if (boundedField.isInputChanged() && boundedField.canWrite(accessModel)) {
          boundedField.writeInto(originalCopy);
        }
      } catch (CannotParseException e) {
        throw new CannotParseException();
      }
    }
    return originalCopy;
  }

  public P getDataIgnoreWrongInput() {
    P originalCopy = Tools.clone(original);
    for (Bounded<P, ?> boundedField : boundedFields) {
      try {
        if (boundedField.isInputChanged() && boundedField.canWrite(accessModel)) {
          boundedField.writeInto(originalCopy);
        }
      } catch (CannotParseException ignored) {
      }
    }
    return originalCopy;
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
      if (!field.validInput()) {
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

  public void persistAsNewEntity(){
    try {
      Tools.add(Proxy.removeProxy(getData()));
      JOptionPane.showMessageDialog(null,"Das Objeckt wurde erfolgreich persistiert");
    } catch (CannotParseException e) {
      markErrors();
      JOptionPane.showMessageDialog(null,"Die folgenden Felder wurden nicht korrekt ausgefüllt");
    }
  }

  public void persistChanges(){
    try {
      P data = getData();
      Tools.edit(Tools.getId(original),(Proxy.removeProxy(data)));
    } catch (CannotParseException e) {
      markErrors();
      JOptionPane.showMessageDialog(null,"Die folgenden Felder wurden nicht korrekt ausgefüllt");
    }
  }

  public void applyMode(Mode mode) {
    switch (mode){
      case REMOVE:
        Tools.delete(original.getClass(),Tools.getId(original));
        break;
      case EDIT:
        persistChanges();
        break;
      case ADD:
        persistAsNewEntity();
        break;
    }
  }
}
