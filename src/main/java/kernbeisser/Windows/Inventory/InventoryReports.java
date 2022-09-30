package kernbeisser.Windows.Inventory;

import com.google.common.collect.Sets;
import kernbeisser.Useful.Named;

import java.util.Set;

public enum InventoryReports implements Named {
  SHELFOVERVIEW("Regalübersicht"),
  SHELFDETAILS("Regaldetails"),
  COUNTINGLISTS("Zähllisten"),
  INVENTORYSHELFRESULTS("Inventurergebnisse nach Regal"),
  INVENTORYRESULT("Inventurergebnis");

  private final String name;

  public static Set<InventoryReports> shelfSelectionAllowed() {
    return Sets.newHashSet(COUNTINGLISTS, INVENTORYSHELFRESULTS);
  }
  InventoryReports(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public String getName() {
    return null;
  }
}
