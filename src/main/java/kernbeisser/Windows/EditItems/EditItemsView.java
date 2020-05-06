package kernbeisser.Windows.EditItems;

import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditItemsView implements View<EditItemsController> {


    private JPanel main;
    private JButton choosePriceList;
    private ObjectViewView<Article> objectView;

    private final EditItemsController controller;

    public EditItemsView(EditItemsController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(EditItemsController controller) {
        choosePriceList.addActionListener(e -> controller.openPriceListSelection());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    private void createUIComponents() {
        objectView = controller.getObjectView();
    }
}
