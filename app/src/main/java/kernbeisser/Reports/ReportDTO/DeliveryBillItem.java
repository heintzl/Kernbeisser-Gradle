package kernbeisser.Reports.ReportDTO;

import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Enums.Delivery;
import kernbeisser.Useful.Tools;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DeliveryBillItem {

  @NotNull private final String orderBy;
  @NotNull private final String name;
  @NotNull private final String kkNumber;
  private final double netPrice;
  private final int amount;
  @NotNull private final String comment;
  private final boolean delivered;
  @NotNull private String expectedDelivery;
  @NotNull private final String placedBy;
  @NotNull private final String alternativeFor;

  private DeliveryBillItem(
      @NotNull String orderBy,
      @NotNull String name,
      @NotNull String kkNumber,
      double netPrice,
      int amount,
      @NotNull String comment,
      boolean delivered,
      @NotNull String expectedDelivery,
      @NotNull String placedBy,
      @NotNull String alternativeFor) {
    this.orderBy = orderBy;

    this.name = name;
    this.kkNumber = kkNumber;
    this.netPrice = netPrice;
    this.amount = amount;
    this.comment = comment;
    this.delivered = delivered;
    this.expectedDelivery = expectedDelivery;
    this.placedBy = placedBy;
    this.alternativeFor = alternativeFor;
  }

  public static DeliveryBillItem ofPreOrder(PreOrder preOrder) {
    CatalogEntry deliveredEntry;
    String alternativeFor = "";
    if (preOrder.getDeliveryState() == Delivery.ALTERNATIVE_DELIVERED) {
      deliveredEntry =
          Tools.ifNull(preOrder.getAlternativeCatalogEntry(), preOrder.getCatalogEntry());
      alternativeFor = "Ersatz für %s".formatted(preOrder.getCatalogEntry().getArtikelNr());
    } else {
      deliveredEntry = preOrder.getCatalogEntry();
    }
    String expectedDelivery = "";
    if (preOrder.getDeliveryState() == Delivery.UNDELIVERED) {
      expectedDelivery = preOrder.getDueDateAsString();
    }

    return new DeliveryBillItem(
        preOrder.getUser().getFullName(),
        "%s %s".formatted(deliveredEntry.getBestelleinheit(), deliveredEntry.getBezeichnung()),
        deliveredEntry.getArtikelNr(),
        deliveredEntry.getPreis() * deliveredEntry.getBestelleinheitsMenge(),
        preOrder.getAmount(),
        Tools.ifNull(preOrder.getComment(), ""),
        preOrder.getDeliveryState() != Delivery.UNDELIVERED,
        expectedDelivery,
        preOrder.getCreationType().getIconCode(),
        alternativeFor);
  }
}
