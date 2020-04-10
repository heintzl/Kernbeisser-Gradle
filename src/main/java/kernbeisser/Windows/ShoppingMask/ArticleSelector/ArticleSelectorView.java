package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
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

    public ArticleSelectorView(ArticleSelectorController controller, Key... required) {
        this.controller = controller;
    }

    boolean searchOnlyWithoutBarcode(){
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
        return new Dimension(500,600);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
