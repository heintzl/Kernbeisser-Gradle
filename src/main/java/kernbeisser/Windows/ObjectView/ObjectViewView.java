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

    ObjectViewView(Window current,ObjectViewController<T> controller){
        super(current);
        add(main);
        add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS,20, Color.GREEN));
        edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL,20,Color.ORANGE));
        delete.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH,20,Color.RED));
        add.addActionListener(e -> controller.add());
        edit.addActionListener(e -> controller.edit());
        delete.addActionListener(e -> controller.delete());
        back.addActionListener(e -> back());
        pack();
        setLocationRelativeTo(null);
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
