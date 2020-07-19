package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ArticleSelectorView implements View<ArticleSelectorController> {
    private JPanel main;
    private JButton chooseButton;
    private JCheckBox onlyWithoutBarcode;
    private SearchBoxView<Article> searchBox;

    private final ArticleSelectorController controller;

    public ArticleSelectorView(ArticleSelectorController controller, PermissionKey... required) {
        this.controller = controller;
    }

    boolean searchOnlyWithoutBarcode() {
        return onlyWithoutBarcode.isSelected();
    }

    private void createUIComponents() {
        searchBox = controller.getSearchBoxView();
    }

    @Override
    public void initialize(ArticleSelectorController controller) {
        onlyWithoutBarcode.addActionListener(e -> controller.refreshLoadSolutions());
        chooseButton.addActionListener(e -> controller.choose());
    }

    @Override
    public @NotNull Dimension getSize() {
        return new Dimension(500, 600);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
