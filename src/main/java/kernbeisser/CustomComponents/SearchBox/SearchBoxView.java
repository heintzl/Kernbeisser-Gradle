package kernbeisser.CustomComponents.SearchBox;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class SearchBoxView <T> extends JPanel implements View {
    private JButton search;
    private JTextField searchInput;
    private ObjectTable<T> objects;
    private JPanel main;


    SearchBoxView(SearchBoxController controller){
        add(main);
        search.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH,14,new Color(0x757EFF)));
        objects.addSelectionListener(e -> controller.select());
        search.addActionListener(e -> controller.search());
        searchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.search();
            }
        });
    }

    void setObjects(Collection<T> objects){
        this.objects.setObjects(objects);
        repaint();
    }

    void setColumns(Collection<Column<T>> columns){
        objects.setColumns(columns);
    }

    String getSearch(){
        return searchInput.getText();
    }

    public T getSelectedObject() {
        return objects.getSelectedObject();
    }

    private void createUIComponents() {
        objects = new ObjectTable<>();
    }
}
