package kernbeisser.Windows.CollectionView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
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
    private JButton add;
    private JButton addAll;
    private JButton removeAll;
    private JButton remove;
    private JPanel moveSec;
    private JScrollPane availableSec;

    @Override
    public void initialize(CollectionController<T> controller) {
        available.addSelectionListener(new ObjectSelectionListener<T>() {
            T last;
            @Override
            public void selected(T e) {
                if(e.equals(last))
                controller.selectAvailable();
                else last = e;
            }
        });
        chosen.addSelectionListener(
                new ObjectSelectionListener<T>() {
                    T last;
                    @Override
                    public void selected(T e) {
                        if(e.equals(last))
                            controller.selectChosen();
                        else last = e;
                    }
                }
        );
        add.addActionListener(e -> controller.selectAvailable());
        addAll.addActionListener(e -> controller.selectAllAvailable());
        remove.addActionListener(e -> controller.selectChosen());
        removeAll.addActionListener(e -> controller.selectAllChosen());
        commit.addActionListener(e -> controller.commit());
        cancel.addActionListener(e -> back());
    }

    void setEditable(boolean editable){
        availableSec.setVisible(editable);
        moveSec.setVisible(editable);
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
        available.add(t);
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
