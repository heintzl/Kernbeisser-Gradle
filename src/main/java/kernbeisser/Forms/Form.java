package kernbeisser.Forms;

import java.util.function.Supplier;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Tools;

public interface Form<T> {
  PermissionKey[] addPermission();

  PermissionKey[] editPermission();

  PermissionKey[] removePermission();

  ObjectForm<T> getObjectContainer();

  Supplier<T> defaultFactory();

  default void remove(T t) {
    Tools.delete(t);
  }
}
