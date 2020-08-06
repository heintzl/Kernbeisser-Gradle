package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.CannotParseException;

public interface ObjectValidator <T> {
    T validate(T input) throws CannotParseException;
}
