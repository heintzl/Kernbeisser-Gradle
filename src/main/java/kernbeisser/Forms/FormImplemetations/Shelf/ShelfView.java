package kernbeisser.Forms.FormImplemetations.Shelf;

import java.util.Set;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingCollectionEditor;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Components.DataListener;
import kernbeisser.Forms.ObjectForm.ObjectForm;
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
  @Getter private ObjectForm<Shelf> objectForm;

  private ObjectForm<Shelf> createObjectForm() {
    return new ObjectForm<>(
        shelfLocation,
        shelfComment,
        editShelfPriceLists,
        extraArticles,
        new DataListener<>(Shelf::getPriceLists, shelfPriceLists::setObjects),
        new DataListener<>(Shelf::getArticles, shelfExtraArticles::setObjects));
  }

  @Override
  public void initialize(ShelfController controller) {
    objectForm = createObjectForm();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void refreshPriceListTable() {
    shelfPriceLists.setObjects(editShelfPriceLists.getData());
  }

  private void refreshExtraArticleTable() {
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
            .withSearchbox(CollectionView.BOTH);
    shelfExtraArticles =
        new ObjectTable<>(
            Columns.create("Artikelname", Article::getName),
            Columns.create("Artikelnummer", Article::getKbNumber),
            Columns.create("Artikelpreisliste", Article::getPriceList));
    extraArticles =
        new AccessCheckingCollectionEditor<>(
                Shelf::getArticles,
                ShelfController.articlesNotInPriceLists(() -> editShelfPriceLists.getData()),
                Columns.create("Artikelname", Article::getName),
                Columns.create("Artikelnummer", Article::getKbNumber),
                Columns.create("Artikelpreisliste", Article::getPriceList))
            .withCloseEvent(this::refreshExtraArticleTable)
            .withSearchbox(CollectionView.BOTH);
    shelfComment =
        new AccessCheckingField<>(Shelf::getComment, Shelf::setComment, AccessCheckingField.NONE);
    shelfLocation =
        new AccessCheckingField<>(Shelf::getLocation, Shelf::setLocation, AccessCheckingField.NONE);
  }
}
