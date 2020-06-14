package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AccessCheckingField <P,V> extends JTextField {

    private final FocusListener noReadPermissionMaker = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            e.getComponent().setForeground(UIManager.getColor ( "Label.foreground"));
            ((JTextComponent)e.getComponent()).setText("");
            e.getComponent().removeFocusListener(this);
        }
    };

    private final ProtectedAccessSetter<P,V> setter;
    private final ProtectedAccessGetter<P,V> getter;

    private final StringTransformer<V> stringTransformer;

    AccessCheckingField(ProtectedAccessGetter<P,V> getter, ProtectedAccessSetter<P,V> setter,
                        StringTransformer<V> stringTransformer){
        this.getter = getter;
        this.setter = setter;
        this.stringTransformer = stringTransformer;
    }

    void setValue(P data) {
        try {
            setText(stringTransformer.toString(getter.get(data)));
        } catch (AccessDeniedException e) {
            setText("Keine Leseberechtigung");
            setForeground(Color.RED);
            addFocusListener(noReadPermissionMaker);
        }
    }

    void putOn(P p) throws CannotParseException {
        try {
            setter.set(p,stringTransformer.fromString(getText()));
            setEnabled(true);
        } catch (AccessDeniedException e) {
            setEnabled(false);
        }
    }

    interface StringTransformer <V>{
        String toString(V v);
        V fromString(String s) throws CannotParseException;
    }

    interface ProtectedAccessGetter <P,V>{
        V get(P p) throws AccessDeniedException;
    }

    interface ProtectedAccessSetter <P,V>{
        void set(P p,V t) throws AccessDeniedException;
    }
}
