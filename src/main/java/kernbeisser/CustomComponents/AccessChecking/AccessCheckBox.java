package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;

import javax.swing.*;
import java.awt.*;

public class AccessCheckBox <P> extends JCheckBox implements Bounded<P>{
    private final Setter<P,Boolean> setter;
    private final Getter<P,Boolean> getter;

    public AccessCheckBox(Getter<P,Boolean> getter, Setter<P,Boolean> setter){
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void setValue(P data) {
        try {
            setSelected(getter.get(data));
        } catch (AccessDeniedException e) {
            setText(getText() + "[Unbekannt]");
        }
    }

    @Override
    public void putOn(P p) {
        try {
            setter.set(p,isSelected());
            setEnabled(true);
        } catch (AccessDeniedException e) {
            setEnabled(false);
        }
    }
}
