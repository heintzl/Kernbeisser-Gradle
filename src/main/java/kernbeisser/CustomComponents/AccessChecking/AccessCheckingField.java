package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Enums.Colors;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AccessCheckingField <P,V> extends JTextField implements Bounded<P,V> {

    private final FocusListener noReadPermissionMaker = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            e.getComponent().setForeground(UIManager.getColor ( "Label.foreground"));
            ((JTextComponent)e.getComponent()).setText("");
            e.getComponent().removeFocusListener(this);
        }
    };

    private final Setter<P,V> setter;
    private final Getter<P,V> getter;

    private final StringTransformer<V> stringTransformer;

    public AccessCheckingField(Getter<P,V> getter, Setter<P,V> setter,
                               StringTransformer<V> stringTransformer){
        this.getter = getter;
        this.setter = setter;
        this.stringTransformer = stringTransformer;
    }

    @Override
    public void setObjectData(P data) {
        try {
            setText(stringTransformer.toString(getter.get(data)));
        } catch (AccessDeniedException e) {
            setText("Keine Leseberechtigung");
            setForeground(Color.RED);
            addFocusListener(noReadPermissionMaker);
        }
    }
    @Override
    public void writeInto(P p) throws CannotParseException {
        try {
            setter.set(p,stringTransformer.fromString(getText()));
        } catch (AccessDeniedException ignored) {

        }
    }

    @Override
    public Getter<P,V> getGetter() {
        return getter;
    }

    @Override
    public Setter<P,V> getSetter() {
        return setter;
    }

    public void setWriteable(boolean b){
        setEnabled(b);
    }

    public void setReadable(boolean b){
        if(!b){
            setText("Keine Leseberechtigung");
            setForeground(Color.RED);
            addFocusListener(noReadPermissionMaker);
        }else {
            if (getText().equals("Keine Leseberechtigung")) setText("");
            setForeground(Colors.LABEL_FOREGROUND.getColor());
            removeFocusListener(noReadPermissionMaker);
        }
    }


    @Override
    public void markWrongInput() {
        Tools.showHint(this);
    }

    @Override
    public boolean validInput() {
        try {
            stringTransformer.fromString(getText());
            return true;
        } catch (CannotParseException e) {
            return false;
        }
    }

    interface StringTransformer <V>{
        String toString(V v);
        V fromString(String s) throws CannotParseException;
    }

    public final static StringTransformer<Integer> INT_FORMER = new StringTransformer<Integer>() {
        @Override
        public String toString(Integer integer) {
            return integer.toString();
        }

        @Override
        public Integer fromString(String s) throws CannotParseException {
            try {
                return Integer.parseInt(s);
            }catch (NumberFormatException e){
                throw new CannotParseException();
            }
        }
    };

    public final static StringTransformer<Double> DOUBLE_FORMER = new StringTransformer<Double>() {
        @Override
        public String toString(Double integer) {
            return integer.toString();
        }

        @Override
        public Double fromString(String s) throws CannotParseException {
            try {
                return Double.parseDouble(s);
            }catch (NumberFormatException e){
                throw new CannotParseException();
            }
        }
    };

    public final static StringTransformer<String> NONE = new StringTransformer<String>() {
        @Override
        public String toString(String s) {
            return s;
        }

        @Override
        public String fromString(String s) {
            return s;
        }
    };

    public final static StringTransformer<Long> LONG_FORMER = new StringTransformer<Long>() {
        @Override
        public String toString(Long aLong) {
            return aLong.toString();
        }

        @Override
        public Long fromString(String s) throws CannotParseException {
            try {
                return Long.parseLong(s);
            }catch (NumberFormatException e){
                throw new CannotParseException();
            }
        }
    };

    public final static StringTransformer<String> EMAIL_FORMER = new StringTransformer<String>() {
        @Override
        public String toString(String s) {
            return s;
        }

        @Override
        public String fromString(String s) throws CannotParseException {
            if(!s.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
                throw new CannotParseException();
            return s;
        }
    };
}
