package kernbeisser.CustomComponents;

import kernbeisser.Tools;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ObjectTable <T> extends JTable {
    private ArrayList<ObjectSelectionListener<T>> selectionListeners = new ArrayList<>();
    private ArrayList<T> objects = new ArrayList<>();
    private Field[] fields;
    private HashMap<Integer,ColumnTransformer> transformers = new HashMap<>();
    private HashMap<Integer,String> columnNames = new HashMap<>();
    public ObjectTable(Field... fields){
        this(null,fields);
    }
    ObjectTable(Collection<T> fill, Field... fields){
        this.fields=fields;
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
    public void setColumnName(String name,int columnId){
        columnNames.put(columnId,name);
    }
    public void addColumnTransformer(ColumnTransformer transformer,int columnId){
        if(!(columnId>fields.length))
            transformers.put(columnId,transformer);
    }
    public int indexOf(T t){
        return objects.lastIndexOf(t);
    }
    public boolean contains(T t){
        return objects.contains(t);
    }
    public T get(T t){
        for (T object : objects) {
            if(object.hashCode()==t.hashCode())return object;
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
    }
    public void remove(int id){
        objects.remove(id);
    }
    public void repaintUI(){
        Object[][] values = new Object[objects.size()][fields.length];
        for (int c = 0; c < fields.length; c++) {
            try {
                fields[c].setAccessible(true);
                ColumnTransformer transformer = transformers.get(c);
                if(transformer!=null) {
                    for (int i = 0; i < objects.size(); i++) {
                        values[i][c] = transformer.transform(fields[c].get(objects.get(i)).toString());
                    }
                }else {
                    for (int i = 0; i < objects.size(); i++) {
                        values[i][c] = fields[c].get(objects.get(i));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String[] names = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String customName = columnNames.get(i);
            if(customName==null){
                names[i]=fields[i].getName();
            }else {
                names[i]=customName;
            }
        }
        setModel(new DefaultTableModel(values,names){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
}
