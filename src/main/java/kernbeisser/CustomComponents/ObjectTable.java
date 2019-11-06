package kernbeisser.CustomComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class ObjectTable <T> extends JTable {
    private ArrayList<ObjectSelectionListener<T>> selectionListeners = new ArrayList<>();
    private ArrayList<T> objects = new ArrayList<>();
    private ArrayList<Column<T>> columns = new ArrayList<>();
    public ObjectTable(Collection<Column<T>> columns){
        this.columns.addAll(columns);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(getSelectedRow()==-1)return;
                T selected = objects.get(getSelectedRow());
                for (ObjectSelectionListener<T> listener : selectionListeners) {
                    listener.selected(selected);
                }
            }
        });
    }
    public ObjectTable(Column<T> ... columns){
        this.columns.addAll(Arrays.asList(columns));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(getSelectedRow()==-1)return;
                T selected = objects.get(getSelectedRow());
                for (ObjectSelectionListener<T> listener : selectionListeners) {
                    listener.selected(selected);
                }
            }
        });
    }
    ObjectTable(Collection<T> fill, Collection<Column<T>> columns){
        this.columns.addAll(columns);
        if(fill!=null)
            objects.addAll(fill);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(getSelectedRow()==-1)return;
                T selected = objects.get(getSelectedRow());
                for (ObjectSelectionListener<T> listener : selectionListeners) {
                    listener.selected(selected);
                }
            }
        });
        repaintUI();
    }
    public void addColumn(Column<T> column){
        columns.add(column);
    }
    public int indexOf(T t){
        return objects.lastIndexOf(t);
    }
    public boolean contains(T t){
        return objects.contains(t);
    }
    public T getSelectedObject(){return objects.get(getSelectedRow());}
    public T get(Function<T,Boolean> function){
        for (T object : objects) {
            if(function.apply(object))return object;
        }
        return null;
    }
    public void addSelectionListener(ObjectSelectionListener<T> listener){
        selectionListeners.add(listener);
    }
    public void addAll(Collection<T> in){
        objects.addAll(in);
        repaintUI();
    }
    public void add(T in){
        objects.add(in);
        repaintUI();
    }
    public void remove(T t){
        objects.remove(t);
        repaintUI();
    }
    public void remove(int id){
        objects.remove(id);
        repaintUI();
    }
    public Collection<T> getItems(){
        return objects;
    }
    public void clear(){
        objects.clear();
        repaintUI();
    }
    public void repaintUI(){
        Object[][] values = new Object[objects.size()][columns.size()];
        for (int c = 0; c < columns.size(); c++) {
            for (int i = 0; i < objects.size(); i++) {
                values[i][c] = columns.get(c).getValue(objects.get(i));
            }
        }
        String[] names = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            names[i]=columns.get(i).getName();
        }
        setModel(new DefaultTableModel(values,names){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
}
