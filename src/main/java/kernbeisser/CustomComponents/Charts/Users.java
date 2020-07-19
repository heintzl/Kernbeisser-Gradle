package kernbeisser.CustomComponents.Charts;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import org.jfree.chart.JFreeChart;

import javax.persistence.EntityManager;
import java.time.YearMonth;
import java.util.HashMap;

public class Users {

    public static JFreeChart createBuyChart(User user, YearMonth from, YearMonth to) {
        EntityManager em = DBConnection.getEntityManager();
        HashMap<YearMonth,Integer> result = new HashMap<>();
        for (Purchase purchase : em.createQuery(
                "select p from Purchase p where p.session.customer.id = :uid and p.createDate between " + from + " and " + to,
                Purchase.class)
                                   .setParameter("uid", user.getId())
                                   .getResultList()) {

            YearMonth month = YearMonth.from(purchase.getCreateDate());
            if (result.containsKey(month)) {
                result.replace(month, result.get(month) + 1);
            } else {
                result.put(month, 1);
            }
        }

        return Graph.generateChart("Einkäufe", "Monat", "Anzahl", Graph.getYearMonthGraph("", from, to, e -> {
            Integer value = result.get(e);
            if (value == null) {
                return 0.;
            }
            return Double.valueOf(value);
        }));
    }

}
