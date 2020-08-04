package kernbeisser.CustomComponents.Charts;

import java.time.YearMonth;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import org.jfree.chart.JFreeChart;

public class Users {

  public static JFreeChart createBuyChart(User user, YearMonth from, YearMonth to) {
    EntityManager em = DBConnection.getEntityManager();
    HashMap<YearMonth, Integer> result = new HashMap<>();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Purchase> criteriaQuery = cb.createQuery(Purchase.class);
    Root<Purchase> root = criteriaQuery.from(Purchase.class);
    criteriaQuery
        .select(root)
        .where(
            cb.and(
                cb.between(
                    root.get("createDate"),
                    Tools.toDate(from).toInstant(),
                    Tools.toDate(to).toInstant()),
                cb.equal(root.get("sid"), user.getId())));

    for (Purchase purchase : em.createQuery(criteriaQuery).getResultList()) {
      YearMonth month = YearMonth.from(purchase.getCreateDate());
      if (result.containsKey(month)) {
        result.replace(month, result.get(month) + 1);
      } else {
        result.put(month, 1);
      }
    }

    return Graph.generateChart(
        "Einkäufe",
        "Monat",
        "Anzahl",
        Graph.getYearMonthGraph(
            "",
            from,
            to,
            e -> {
              Integer value = result.get(e);
              if (value == null) {
                return 0.;
              }
              return Double.valueOf(value);
            }));
  }
}
