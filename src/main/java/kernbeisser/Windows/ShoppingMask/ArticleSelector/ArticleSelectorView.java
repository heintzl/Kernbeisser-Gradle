package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class ArticleSelectorView extends Window implements View {
    private JPanel main;
    private JButton chooseButton;
    private JCheckBox onlyWithoutBarcode;
    private SearchBoxView<Article> searchBox;

    private final ArticleSelectorController controller;

    public ArticleSelectorView(Window currentWindow,ArticleSelectorController controller, Key... required) {
        super(currentWindow, required);
        this.controller = controller;
        add(main);
        onlyWithoutBarcode.addActionListener(e -> controller.refreshLoadSolutions());
        chooseButton.addActionListener(e -> controller.choose());
        setSize(500,600);
        windowInitialized();
    }

    boolean searchOnlyWithoutBarcode(){
        return onlyWithoutBarcode.isSelected();
    }

    private void createUIComponents() {
        searchBox = controller.getSearchBoxView();
    }
}
