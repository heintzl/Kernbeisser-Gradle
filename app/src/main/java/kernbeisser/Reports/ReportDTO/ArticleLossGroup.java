package kernbeisser.Reports.ReportDTO;

import lombok.Data;
import lombok.Setter;

@Data
public class ArticleLossGroup {

  private final int number;
  private final String name;
  @Setter private double netPurchaseSum;
  @Setter private double netRetailSum;
  @Setter private double grossRetailSum;
  @Setter private int count;

  public ArticleLossGroup(int number, String name) {
    this.number = number;
    this.name = name;
    netPurchaseSum = 0.0;
    netRetailSum = 0.0;
    grossRetailSum = 0.0;
    count = 0;
  }
}
