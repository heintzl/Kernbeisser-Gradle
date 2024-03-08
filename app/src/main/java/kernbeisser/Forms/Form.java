package kernbeisser.Forms;

import java.util.function.Supplier;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Useful.Tools;

public interface Form<T extends ActuallyCloneable> {
  void addPermission();

  void editPermission();

  void removePermission();

  ObjectForm<T> getObjectContainer();

  Supplier<T> defaultFactory();

  default void remove(T t) {
    Tools.delete(t);
  }
}
