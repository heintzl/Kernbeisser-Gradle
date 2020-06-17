package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;

public interface Bounded <T>{

    void setValue(T data);

    void putOn(T p) throws CannotParseException;
}
