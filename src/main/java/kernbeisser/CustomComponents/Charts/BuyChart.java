package kernbeisser.CustomComponents.Charts;

import java.time.YearMonth;
import kernbeisser.DBEntities.User;
import org.jfree.chart.ChartPanel;

public class BuyChart extends ChartPanel {
  public BuyChart(User user, YearMonth from, YearMonth to) {
    super(Users.createBuyChart(user, from, to));
  }
}
