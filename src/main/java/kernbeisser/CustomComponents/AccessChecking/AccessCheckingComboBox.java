package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.AccessDeniedException;

import javax.swing.*;
import java.util.Collection;

public class AccessCheckingComboBox <P,V> extends JComboBox<V> implements Bounded<P>{
    private final Setter<P,V> setter;
    private final Getter<P,V> getter;

    public AccessCheckingComboBox(Getter<P,V> getter, Setter<P,V> setter){
        this.getter = getter;
        this.setter = setter;
    }

    public void setItems(Collection<V> values){
        removeAllItems();
        values.forEach(super::addItem);
    }

    @Override
    public void setValue(P data) {
        try {
            setSelectedItem(getter.get(data));
        } catch (AccessDeniedException ignored) {

        }
    }

    @Override
    public void putOn(P p) {
        try {
            setter.set(p,getItemAt(getSelectedIndex()));
            setEnabled(true);
        } catch (AccessDeniedException e) {
            setEnabled(false);
        }
    }
}
