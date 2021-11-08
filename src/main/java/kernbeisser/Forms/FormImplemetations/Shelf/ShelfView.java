package kernbeisser.Forms.FormImplemetations.Shelf;

import java.util.Set;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingCollectionEditor;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Components.DataListener;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Windows.MVC.IView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ShelfView implements IView<ShelfController> {
  private JPanel main;
  private ObjectTable<PriceList> shelfPriceLists;
  private AccessCheckingField<Shelf, String> shelfLocation;
  private AccessCheckingField<Shelf, String> shelfComment;
  private AccessCheckingCollectionEditor<Shelf, Set<PriceList>, PriceList> editShelfPriceLists;

  @Getter(lazy = true)
  private final ObjectForm<Shelf> objectForm = createObjectForm();

  private ObjectForm<Shelf> createObjectForm() {
    return new ObjectForm<>(
        shelfLocation,
        shelfComment,
        editShelfPriceLists,
        new DataListener<>(Shelf::getPriceLists, shelfPriceLists::setObjects));
  }

  private final Column<PriceList>[] priceListColumns =
      new Column[] {Columns.create("Preisliste", PriceList::getName)};

  @Override
  public void initialize(ShelfController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    shelfPriceLists = new ObjectTable<>(priceListColumns);
    editShelfPriceLists =
        new AccessCheckingCollectionEditor<>(
            Shelf::getPriceLists, Source.of(PriceList.class), priceListColumns);
    shelfComment =
        new AccessCheckingField<>(Shelf::getComment, Shelf::setComment, AccessCheckingField.NONE);
    shelfLocation =
        new AccessCheckingField<>(Shelf::getLocation, Shelf::setLocation, AccessCheckingField.NONE);
  }
}
