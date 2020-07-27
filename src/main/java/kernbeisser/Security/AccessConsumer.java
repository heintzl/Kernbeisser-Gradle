package kernbeisser.Security;

import kernbeisser.Exeptions.AccessDeniedException;

public interface AccessConsumer<T> {
  void accept(T t) throws AccessDeniedException;
}
