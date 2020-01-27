package kernbeisser.Windows.ObjectView;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ObjectViewView <T> extends Window {
    private ObjectTable<T> objectTable;
    private JButton add;
    private JButton edit;
    private JButton back;
    private JButton delete;
    private JPanel main;
    private JButton refresh;

    private ObjectViewController<T> controller;

    ObjectViewView(Window current,ObjectViewController<T> controller){
        super(current);
        this.controller=controller;
        add(main);
        objectTable.addSelectionListener((e) -> controller.select());
        add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS,20, new Color(71, 189, 23)));
        edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL,20,new Color(69, 189, 174)));
        delete.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH,20,new Color(189, 101, 85)));
        refresh.setIcon(IconFontSwing.buildIcon(FontAwesome.REFRESH,20,new Color(56, 64, 189)));
        add.addActionListener(e -> controller.add());
        edit.addActionListener(e -> controller.edit());
        delete.addActionListener(e -> controller.delete());
        back.addActionListener(e -> back());
        refresh.addActionListener(e -> controller.refresh());
        pack();
        setLocationRelativeTo(null);
    }

    void setEditAvailable(boolean s){
        edit.setEnabled(s);
    }

    void setRemoveAvailable(boolean s){
        delete.setEnabled(s);
    }

    void setAddAvailable(boolean s){
        add.setEnabled(s);
    }

    @Override
    protected void open() {
        if(controller!=null)
        controller.refresh();
        super.open();
    }

    T getSelectedObject(){
        return objectTable.getSelectedObject();
    }

    void setObjects(Collection<T> objects){
        objectTable.setObjects(objects);
    }

    void addColumn(Column<T> column){
        objectTable.addColumn(column);
    }

    private void createUIComponents() {
        objectTable = new ObjectTable<>();
    }
}
