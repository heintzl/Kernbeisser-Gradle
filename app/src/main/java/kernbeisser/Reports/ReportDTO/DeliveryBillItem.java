package kernbeisser.Reports.ReportDTO;

import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Useful.Tools;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

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

  private DeliveryBillItem(@NotNull String orderBy, @NotNull String name,
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
    if(preOrder.getAlternativeDelivery() == Boolean.TRUE) {
      deliveredEntry = Tools.ifNull(preOrder.getAlternativeCatalogEntry(), preOrder.getCatalogEntry());
      alternativeFor = preOrder.getCatalogEntry().getArtikelNr();
    } else {
      deliveredEntry = preOrder.getCatalogEntry();
    }
    String expectedDelivery = "";
    if (!preOrder.isDelivered()) {
      expectedDelivery = preOrder.getDueDateAsString();
    }

    return new DeliveryBillItem(
            preOrder.getUser().getFullName(),
            deliveredEntry.getBezeichnung(),
            deliveredEntry.getArtikelNr(),
            deliveredEntry.getPreis(),
            preOrder.getAmount(),
            preOrder.getComment() ,
            preOrder.isDelivered(),
            expectedDelivery,
            preOrder.getCreationType().getName(),
            alternativeFor);
  }
}
