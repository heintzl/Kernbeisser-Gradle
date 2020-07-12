package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CollectionView.CollectionController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.function.Supplier;

public class AccessCheckingCollectionEditor <P,V> extends JButton implements Bounded <P,Collection<V>> {
    private final Getter<P,Collection<V>> getter;
    private final Setter<P,Collection<V>> setter;
    private boolean changed = false;

    private boolean editable;

    private Collection<V> data;

    private final Supplier<Collection<V>> supplier;

    private final Column<V>[] columns;

    public AccessCheckingCollectionEditor(Getter<P,Collection<V>> getter, Setter <P,Collection<V>> setter, Supplier<Collection<V>> source, Column<V> ... columns){
        this.getter = getter;
        this.setter = setter;
        this.supplier = source;
        this.columns = columns;
        addActionListener(this::trigger);
    }


    void trigger(ActionEvent event){
        new CollectionController<V>(data,supplier.get(),editable,columns);
    }

    @Override
    public void inputChanged() {
        changed = true;
    }

    @Override
    public boolean isInputChanged() {
        return changed;
    }

    @Override
    public void setObjectData(P data) {
        try {
            data = (P) getter.get(data);
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeInto(P p) throws CannotParseException {
        try {
            setter.set(p,data);
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markWrongInput() {
        Tools.showUnexpectedErrorWarning(new Exception("Unknown error happened"));
    }

    @Override
    public Getter<P,Collection<V>> getGetter() {
        return getter;
    }

    @Override
    public Setter<P,Collection<V>> getSetter() {
        return setter;
    }

    @Override
    public void setReadable(boolean b) {
        setEnabled(b);
    }

    @Override
    public void setWriteable(boolean b) {
        editable = b;
    }

    @Override
    public boolean validInput() {
        return true;
    }
}
