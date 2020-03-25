package kernbeisser.Windows.ObjectView;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ObjectViewView<T> extends Window {
    private ObjectTable<T> objectTable;
    private JButton add;
    private JButton edit;
    private JButton back;
    private JButton delete;
    private JPanel main;
    private JTextField searchBar;
    private JButton search;
    private IntegerParseField maxResults;

    private ObjectViewController<T> controller;

    ObjectViewView(Window current, ObjectViewController<T> controller) {
        super(current);
        this.controller = controller;
        add(main);
        objectTable.addSelectionListener((e) -> controller.select());
        add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, new Color(71, 189, 23)));
        edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(69, 189, 174)));
        delete.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, 20, new Color(189, 101, 85)));
        search.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(117, 126, 255)));
        add.addActionListener(e -> controller.add());
        edit.addActionListener(e -> controller.edit());
        delete.addActionListener(e -> controller.delete());
        back.addActionListener(e -> back());
        search.addActionListener(e -> controller.refresh());
        searchBar.addActionListener(e -> controller.refresh());
        pack();
        setLocationRelativeTo(null);
    }

    void setEditAvailable(boolean s) {
        edit.setEnabled(s);
    }

    void setRemoveAvailable(boolean s) {
        delete.setEnabled(s);
    }

    void setAddAvailable(boolean s) {
        add.setEnabled(s);
    }

    int getMax() {
        return maxResults.getValue();
    }

    String getSearch() {
        return searchBar.getText();
    }

    @Override
    protected void open() {
        if (controller != null) {
            controller.refresh();
        }
        super.open();
    }

    T getSelectedObject() {
        return objectTable.getSelectedObject();
    }

    void setObjects(Collection<T> objects) {
        objectTable.setObjects(objects);
    }

    void addColumn(Column<T> column) {
        objectTable.addColumn(column);
    }

    private void createUIComponents() {
        objectTable = new ObjectTable<>();
    }

    boolean commitDelete() {
        return JOptionPane.showConfirmDialog(this, "Soll dieses Object wirklich gelöschst werden?") == 0;
    }

}
