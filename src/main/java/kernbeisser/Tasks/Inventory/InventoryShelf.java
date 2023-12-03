package kernbeisser.Tasks.Inventory;

import java.time.LocalDate;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.FieldCondition;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.Setting;
import lombok.Getter;

@Getter
public class InventoryShelf {

  private final Shelf shelf;
  private double netValue;
  private double depositValue;

  public InventoryShelf(Shelf shelf) {
    this.shelf = shelf;
    updateValues();
  }

  private void updateValues() {

    double net = 0;
    double deposit = 0;
    LocalDate date = Setting.INVENTORY_SCHEDULED_DATE.getDateValue();
    for (ArticleStock stock :
        DBConnection.getConditioned(
            ArticleStock.class,
            new FieldCondition("shelf", shelf),
            new FieldCondition("inventoryDate", date))) {
      net += stock.calculateNetPrice();
      deposit += stock.calculateDeposit();
    }
    netValue = net;
    depositValue = deposit;
  }
}
