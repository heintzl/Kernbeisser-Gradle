package kernbeisser.Windows.CollectionView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class CollectionView<T> implements View<CollectionController<T>> {


    private JButton commit;
    private JButton cancel;
    private ObjectTable<T> available;
    private ObjectTable<T> chosen;
    private JPanel main;

    @Override
    public void initialize(CollectionController<T> controller) {
        available.addSelectionListener(e -> controller.selectAvailable());
        chosen.addSelectionListener(e -> controller.selectChosen());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    T getSelectedChosenObject(){
        return chosen.getSelectedObject();
    }

    T getSelectedAvailableObject(){
        return available.getSelectedObject();
    }

    void setColumns(Column<T>[] columns){
        available.setColumns(columns);
        chosen.setColumns(columns);
    }

    void addChosen(T t){
        chosen.add(t);
    }

    void removeAvailable(T t){
        available.remove(t);
    }

    void addAvailable(T t){
        available.remove(t);
    }

    void removeChosen(T t){
        chosen.remove(t);
    }

    void setAvailable(Collection<T> collection){
        available.setObjects(collection);
    }

    private void createUIComponents() {
        available = new ObjectTable<T>();
        chosen = new ObjectTable<T>();
    }

    public void setChosen(Collection<T> loaded) {
        chosen.setObjects(loaded);
    }

    public Collection<T> getAllChosen(){
        return chosen.getItems();
    }
}
