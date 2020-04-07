package kernbeisser.CustomComponents.Charts;

import kernbeisser.DBEntities.User;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.time.YearMonth;

public class BuyChart extends ChartPanel {
    public BuyChart(User user, YearMonth from, YearMonth to) {
        super(Users.createBuyChart(user,from,to));
    }
}
