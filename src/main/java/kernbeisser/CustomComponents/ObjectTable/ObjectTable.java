package kernbeisser.CustomComponents.ObjectTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectTable <T> extends JTable {
    private ArrayList<ObjectSelectionListener<T>> selectionListeners = new ArrayList<>();
    private ArrayList<T> objects = new ArrayList<>();
    private ArrayList<Column<T>> columns = new ArrayList<>();
    private boolean complex = false;

    public ObjectTable(Collection<Column<T>> columns){
        this(null,columns);
    }
    public ObjectTable(Collection<T> fill,Column<T> ... columns){
        this(fill,Arrays.asList(columns));
    }
    public ObjectTable(Column<T> ... columns){
        this(Arrays.asList(columns));
    }

    public void setComplex(boolean v){
        complex = v;
        repaintUI();
    }

    ObjectTable(Collection<T> fill, Collection<Column<T>> columns){
        this.columns.addAll(columns);
        if(fill!=null)
            objects.addAll(fill);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                handleCellComponentEvents();
                if(getSelectedRow()==-1)return;
                T selected = objects.get(getSelectedRow());
                ObjectTable.this.columns.get(getSelectedColumn()).onAction(selected);
                for (ObjectSelectionListener<T> listener : selectionListeners) {
                    listener.selected(selected);
                }
            }
        });
        repaintUI();
    }
    private void handleCellComponentEvents(){
        if(!complex)return;
        Object cell = getValueAt(getSelectedRow(),getSelectedColumn());
        if(!(cell instanceof Component))return;
        Component com = (Component) cell;
        com.setFocusable(true);
        com.requestFocus();
        if(cell instanceof AbstractButton){
            ((AbstractButton) cell).doClick();
        }

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
    public T getSelectedObject(){
        return getSelectedRow() != -1 ? objects.get(getSelectedRow()): null;}
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
        if(complex) {
            for (String name : names) {
                getColumn(name).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    if (value instanceof Component)
                        return (Component) value;
                    else return new JLabel(String.valueOf(value));
                });
            }
        }
    }
    public void setObjects(Collection<T> collection){
        objects=new ArrayList<>(collection);
        repaintUI();
    }
}
