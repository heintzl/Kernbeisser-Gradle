package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.AccessDeniedException;

public interface Getter <P,V>{
    V get(P p) throws AccessDeniedException;
}
