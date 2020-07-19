package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.AccessDeniedException;

public interface Setter<P, V> {
    void set(P p, V t) throws AccessDeniedException;
}
