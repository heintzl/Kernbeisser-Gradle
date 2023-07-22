package kernbeisser.Forms.FormImplemetations.Shelf;

import java.util.Set;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingCollectionEditor;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Components.DataListener;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Icons;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ShelfView implements IView<ShelfController> {
  private JPanel main;
  private ObjectTable<PriceList> shelfPriceLists;
  private ObjectTable<Article> shelfExtraArticles;
  private AccessCheckingField<Shelf, String> shelfLocation;
  private AccessCheckingField<Shelf, String> shelfComment;
  private AccessCheckingCollectionEditor<Shelf, Set<PriceList>, PriceList> editShelfPriceLists;
  private AccessCheckingCollectionEditor<Shelf, Set<Article>, Article> extraArticles;
  private AccessCheckingField<Shelf, Integer> shelfNo;
  private JLabel extraArticleLabel;
  @Getter private ObjectForm<Shelf> objectForm;

  private ObjectForm<Shelf> createObjectForm() {
    return new ObjectForm<>(
        shelfLocation,
        shelfNo,
        shelfComment,
        editShelfPriceLists,
        extraArticles,
        new DataListener<>(Shelf::getPriceLists, shelfPriceLists::setObjects),
        new DataListener<>(Shelf::getArticles, shelfExtraArticles::setObjects));
  }

  @Override
  public void initialize(ShelfController controller) {
    objectForm = createObjectForm();
    objectForm.setObjectDistinction("Das Regal");
    objectForm.registerUniqueCheck(shelfNo, controller::isShelfNoUniqe);
    objectForm.registerUniqueCheck(shelfLocation, controller::isLocationUniqe);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void refreshPriceListTable() {
    shelfPriceLists.setObjects(editShelfPriceLists.getData());
  }

  public void refreshExtraArticleTable() {
    shelfExtraArticles.setObjects(extraArticles.getData());
  }

  private void createUIComponents() {
    shelfPriceLists = new ObjectTable<>(Columns.create("Name", PriceList::getName));
    editShelfPriceLists =
        new AccessCheckingCollectionEditor<>(
                Shelf::getPriceLists,
                PriceList.onlyWithContent(),
                Columns.create("Name", PriceList::getName))
            .withCloseEvent(this::refreshPriceListTable)
            .withSearchbox(CollectionView.AVAILABLE);
    shelfExtraArticles =
        new ObjectTable<>(
            Columns.create("Artikelname", Article::getName),
            Columns.create("Artikelnummer", Article::getKbNumber).withSorter(Column.NUMBER_SORTER),
            Columns.create("Artikelpreisliste", Article::getPriceList));
    extraArticles =
        new AccessCheckingCollectionEditor<>(
                Shelf::getArticles,
                ShelfController.articlesNotInPriceLists(() -> null),
                Columns.create("Artikelname", Article::getName),
                Columns.create("Artikelnummer", Article::getKbNumber)
                    .withSorter(Column.NUMBER_SORTER),
                Columns.create("Artikelpreisliste", Article::getPriceList))
            .withCloseEvent(this::refreshExtraArticleTable)
            .withSearchbox(CollectionView.AVAILABLE);
    shelfComment =
        new AccessCheckingField<>(Shelf::getComment, Shelf::setComment, AccessCheckingField.NONE);
    shelfLocation =
        new AccessCheckingField<>(Shelf::getLocation, Shelf::setLocation, AccessCheckingField.NONE);
    shelfNo =
        new AccessCheckingField<>(
            Shelf::getShelfNo, Shelf::setShelfNo, AccessCheckingField.UNSIGNED_INT_FORMER);
    extraArticleLabel = new JLabel("Extra Artikel");
    extraArticleLabel.setIcon(Icons.barcodeIcon);
  }
}
