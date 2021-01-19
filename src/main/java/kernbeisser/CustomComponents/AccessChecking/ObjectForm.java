package kernbeisser.CustomComponents.AccessChecking;

import javax.swing.*;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;

public class ObjectForm<P> {
  private ObjectValidator<P> objectValidator = input -> input;

  private final ObjectFormComponent<P>[] components;

  private boolean checkInputVerifier = true;

  private P original;
  private P accessModel;

  @SafeVarargs
  public ObjectForm(ObjectFormComponent<P> ... boundedFields) {
    for (ObjectFormComponent<P> boundedField : boundedFields) {
      if (boundedField == null)
        throw new NullPointerException("cannot create ObjectFrom with null fields");
    }

    this.components = boundedFields;
  }

  public void setSource(P data) {
    if (data == null) throw new NullPointerException("cannot set null as source for ObjectView");
    this.original = data;
    this.accessModel = Tools.clone(original);
    refreshAccess();
    setData(data);
  }

  public P getData() throws CannotParseException {
    checkValidSource();
    P originalCopy = Tools.clone(original);
    boolean success = true;
    for (ObjectFormComponent<P> component : components){
        if(component instanceof BoundedWriteProperty) {
          try {
            if (component instanceof Predictable && !((Predictable) component)
                .isPropertyWriteable(accessModel))
              continue;
            ((BoundedWriteProperty<P, ?>) component).set(originalCopy);
          } catch (PermissionKeyRequiredException e) {
            ((BoundedWriteProperty<?, ?>) component).setPropertyEditable(false);
          } catch (CannotParseException e){
            if (!(e instanceof SilentParseException)) {
              ((BoundedWriteProperty<?, ?>) component).setInvalidInput();
            }
            success = false;
          }
      }
    }
    if(!success)throw new CannotParseException();
    return objectValidator.validate(originalCopy);
  }

  private void setData(@NotNull P data) {
    for (ObjectFormComponent<P> boundedField : components) {
      if(boundedField instanceof BoundedReadProperty){
        try {
          if (boundedField instanceof Predictable && !((Predictable) boundedField)
              .isPropertyReadable(accessModel))
            continue;
          ((BoundedReadProperty<P, ?>) boundedField).setValue(data);
        }catch (PermissionKeyRequiredException e){
          ((BoundedReadProperty<?, ?>) boundedField).setReadable(false);
        }
      }
    }
  }

  public void refreshAccess() {
    checkValidSource();
    for (ObjectFormComponent<P> boundedField : components) {
      setAccess(boundedField);
    }
  }

  private void setAccess(ObjectFormComponent<P> component){
    if(component instanceof Predictable){
      if (component instanceof BoundedReadProperty)
        ((BoundedReadProperty<?, ?>) component).setReadable(((Predictable<P>) component).isPropertyReadable(accessModel));
      if(component instanceof BoundedWriteProperty)
        ((BoundedWriteProperty<?, ?>) component).setPropertyEditable(((Predictable<P>) component).isPropertyWriteable(accessModel));
    }
  }

  public boolean persistAsNewEntity() {
    checkValidSource();
    try {
      P data = getData();
      Tools.add(Proxy.removeProxy(data));
      JOptionPane.showMessageDialog(null, "Das Objeckt wurde erfolgreich persistiert");
      return true;
    } catch (CannotParseException e) {
      notifyException(e);
      return false;
    }
  }


  private static void notifyException(CannotParseException e){
    if(!(e instanceof SilentParseException))
      JOptionPane.showMessageDialog(null, "Die folgenden Felder wurden nicht korrekt ausgefüllt");
  }

  public boolean persistChanges() {
    checkValidSource();
    try {
      P data = getData();
      Tools.edit(Tools.getId(original), (Proxy.removeProxy(data)));
      return true;
    } catch (CannotParseException e) {
      notifyException(e);
      return false;
    }
  }

  public boolean applyMode(Mode mode) {
    checkValidSource();
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

  public P getOriginal() {
    return original;
  }

  private void checkValidSource() {
    if (original == null) throw new UnsupportedOperationException("no source specified");
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
