package kernbeisser.Windows.DefaultSearchPanel;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class DefaultSearchPanelView<T> extends JPanel implements View {
    private ObjectTable<T> values;
    private JPanel main;
    private JButton choose;
    private JButton searchButton;
    private IntegerParseField max;
    private PermissionField search;

    DefaultSearchPanelView(DefaultSearchPanelController<T> controller) {
        add(main);
        setSize(500,600);
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

    public Window asWindow(Window current){
        Window out = new Window(current);
        out.add(this);
        return out;
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
