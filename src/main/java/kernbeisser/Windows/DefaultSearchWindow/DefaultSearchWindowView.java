package kernbeisser.Windows.DefaultSearchWindow;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import java.util.Collection;

public class DefaultSearchWindowView <T> extends Window{
    private ObjectTable<T> values;
    private JPanel main;
    private JButton choose;
    private JButton button1;

    DefaultSearchWindowView(Window current,DefaultSearchWindowController<T> controller){
        super(current);
        add(main);
        choose.addActionListener(e -> controller.choose());
    }

    private void createUIComponents() {
        values = new ObjectTable<>();
    }

    T getSelectedValue(){
        return values.getSelectedObject();
    }

    void setColumns(Collection<Column<T>> columns ){
        values.setColumns(columns);
    }
}
