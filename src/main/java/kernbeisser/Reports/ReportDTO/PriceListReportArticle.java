package kernbeisser.Reports.ReportDTO;

import kernbeisser.DBEntities.ShoppingItem;
import lombok.Data;

@Data
public class PriceListReportArticle {

  public static PriceListReportArticle ofShoppingItem(ShoppingItem item) {
    PriceListReportArticle priceListArticle = new PriceListReportArticle();
    priceListArticle.name = item.getName();
    priceListArticle.itemRetailPrice = item.getItemRetailPrice();
    priceListArticle.kbNumber = item.getKbNumber();
    priceListArticle.suppliersShortName = item.getSuppliersShortName();
    priceListArticle.shortBarcode = item.getShortBarcode();
    priceListArticle.suppliersItemNumber = item.getSuppliersItemNumber();
    priceListArticle.metricUnits = item.getMetricUnits().getName();
    priceListArticle.weighAble = item.isWeighAble();
    return priceListArticle;
  }

  private String name;
  private Double itemRetailPrice;
  private Integer kbNumber;
  private String suppliersShortName;
  private String shortBarcode;
  private Integer suppliersItemNumber;
  private String metricUnits;
  private Boolean weighAble;
}
