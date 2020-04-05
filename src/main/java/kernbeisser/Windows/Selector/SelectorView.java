package kernbeisser.Windows.Selector;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Job;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class SelectorView <T> extends Window implements View {
    private JPanel mainPanel;
    private ObjectTable<T> selected;
    private JButton add;
    private JButton remove;
    private JLabel title;


    public SelectorView(Window current, SelectorController<T> controller) {
        super(current);
        add(mainPanel);
        setSize(500, 500);
        setLocationRelativeTo(current);
        add.addActionListener(e -> controller.add());
        add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, Color.GREEN));
        remove.addActionListener(e -> controller.remove());
        remove.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED));
        windowInitialized();
    }

    private void createUIComponents() {
        selected = new ObjectTable<T>();
    }

    void setColumns(Column<T>[] columns){
        selected.setColumns(Arrays.asList(columns));
    }

    void setObjects(Collection<T> collection){
        selected.setObjects(collection);
    }

    T getSelectedValue(){
       return selected.getSelectedObject();
    }

    public void addValue(T e) {
        if(!selected.contains(e))
        selected.add(e);

    }

    public void removeValue(T e) {
        selected.remove(e);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
