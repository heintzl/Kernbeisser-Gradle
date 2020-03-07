package kernbeisser.Windows.DefaultSearchWindow;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import java.awt.*;
import java.util.Collection;

public class DefaultSearchWindowView<T> extends Window implements View {
    private ObjectTable<T> values;
    private JPanel main;
    private JButton choose;
    private JButton searchButton;
    private IntegerParseField max;
    private PermissionField search;

    DefaultSearchWindowView(Window current, DefaultSearchWindowController<T> controller) {
        super(current);
        add(main);
        setSize(500,600);
        setLocationRelativeTo(current);
        choose.setEnabled(false);
        choose.addActionListener(e -> controller.choose());
        search.addActionListener(e -> controller.refresh());
        searchButton.addActionListener(e -> controller.refresh());
        values.addSelectionListener(e -> choose.setEnabled(true));
        searchButton.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH,20,new Color(0x757EFF)));
    }

    private void createUIComponents() {
        values = new ObjectTable<>();
    }

    T getSelectedValue() {
        return values.getSelectedObject();
    }

    void setColumns(Collection<Column<T>> columns) {
        values.setColumns(columns);
    }

    String getSearch() {
        return search.getText();
    }

    int getMax() {
        return max.getValue();
    }

    void setValues(Collection<T> values) {
        this.values.setObjects(values);
    }

    void setChooseEnabled(boolean b) {
        choose.setEnabled(b);
    }
}
