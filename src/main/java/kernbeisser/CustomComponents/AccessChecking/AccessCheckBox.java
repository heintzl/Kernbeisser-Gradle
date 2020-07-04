package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;

import javax.swing.*;

public class AccessCheckBox <P> extends JCheckBox implements Bounded<P,Boolean>{

    private boolean inputChanged = false;

    private final Setter<P,Boolean> setter;
    private final Getter<P,Boolean> getter;

    public AccessCheckBox(Getter<P,Boolean> getter, Setter<P,Boolean> setter){
        this.getter = getter;
        this.setter = setter;
        addActionListener(e -> inputChanged=true);
    }

    @Override
    public void inputChanged() {
        inputChanged = true;
    }

    @Override
    public boolean isInputChanged() {
        return inputChanged;
    }

    @Override
    public void setObjectData(P data) {
        try {
            setSelected(getter.get(data));
        } catch (AccessDeniedException ignored) {}
    }

    @Override
    public void writeInto(P p) {
        try {
            setter.set(p,isSelected());
        } catch (AccessDeniedException ignored) {}
    }

    @Override
    public Getter<P,Boolean> getGetter() {
        return getter;
    }

    @Override
    public Setter<P,Boolean> getSetter() {
        return setter;
    }

    @Override
    public void setReadable(boolean b) {
        if(!b){
            setText(getText() + "[Unbekannt]");
        }else {
            setText(getText().replace("[Unbekannt]",""));
        }
    }

    @Override
    public void setWriteable(boolean b) {
        setEnabled(b);
    }

    @Override
    public void markWrongInput() {
        Tools.showHint(this);
    }

    @Override
    public boolean validInput() {
        return true;
    }
}
