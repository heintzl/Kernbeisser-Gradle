package kernbeisser.CustomComponents.SearchBox;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Optional;
import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class SearchBoxView<T> implements IView<SearchBoxController<T>> {

    private JButton search;
    private JTextField searchInput;
    private ObjectTable<T> objectTable;
    private JPanel main;
    @Getter
    private JPanel extraOptionsPanel;

    @Linked
    private SearchBoxController<T> controller;

    void setObjectTable(Collection<T> objectTable) {
        this.objectTable.setObjects(objectTable);
    }

    void setColumns(Collection<Column<T>> columns) {
        objectTable.setColumns(columns);
    }

    String getSearch() {
        return searchInput.getText();
    }

    public Optional<T> getSelectedObject() {
        return objectTable.getSelectedObject();
    }

    private void createUIComponents() {
        objectTable = new ObjectTable<>();
    }

    @Override
    public void initialize(SearchBoxController<T> controller) {
        search.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 14, new Color(0x757EFF)));
        search.addActionListener(e -> controller.invokeSearch());
        searchInput.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        controller.invokeSearch();
                    }
                });
    }

    void addSelectionListener(ObjectSelectionListener<T> selectionListener) {
        objectTable.addSelectionListener(selectionListener);
    }

    void addDoubleClickListener(ObjectSelectionListener<T> doubleSelectionListener) {
        objectTable.addDoubleClickListener(doubleSelectionListener);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    @Override
    public @NotNull Dimension getSize() {
        return main.getSize();
    }

    public void setSearch(String s) {
        searchInput.setText(s);
    }

    @Override
    public Component getFocusOnInitialize() {
        return searchInput;
    }

    public void setRowFilter(RowFilter<T> rowFilter) {
        objectTable.setRowFilter(rowFilter);
    }

    public ObjectTable<T> getObjectTable() {
        return objectTable;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        main.add(scrollPane1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setViewportView(objectTable);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 50), 0, false));
        searchInput = new JTextField();
        panel1.add(searchInput, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(350, -1), new Dimension(500, -1), 0, false));
        search = new JButton();
        search.setText("");
        panel1.add(search, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        main.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        extraOptionsPanel = new JPanel();
        extraOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        main.add(extraOptionsPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }
}
