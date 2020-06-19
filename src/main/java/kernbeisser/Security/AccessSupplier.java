package kernbeisser.Security;

import kernbeisser.Exeptions.AccessDeniedException;

public interface AccessSupplier <T>{
    T get() throws AccessDeniedException;
}
