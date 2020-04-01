package kernbeisser.CustomComponents.Charts;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Users {
    public static JFreeChart createBuyChart(User user, YearMonth from, YearMonth to){
        EntityManager em = DBConnection.getEntityManager();
        HashMap<YearMonth,Integer> result = new HashMap<>();
        for (Purchase purchase : em.createQuery(
                "select p from Purchase p where p.session.customer.id = :uid and p.createDate between :from and :to",
                Purchase.class)
                                   .setParameter("uid", user.getId())
                                   .setParameter("from", from)
                                   .setParameter("to", to).getResultList()) {

            YearMonth month = YearMonth.from(purchase.getCreateDate().toLocalDate());
            if (result.containsKey(month)) {
                result.replace(month,result.get(month)+1);
            }else {
                result.put(month,1);
            }
        }

        XYSeries data = create("Einkäufe", from,to,e -> Double.valueOf(result.get(e)));
        em.close();
        return ChartFactory.createXYLineChart("Einkäufe", "Monat", "Anzahl", new XYSeriesCollection(data));
    }
    private static XYSeries create(String title,YearMonth from, YearMonth to, Function<YearMonth,Double> function){
        XYSeries data = new XYSeries(title);
        forEachMonthBetween(from, to, e -> data.add(e.getMonthValue(), function.apply(e)));
        return data;
    }

    private static void forEachMonthBetween(YearMonth from, YearMonth to, Consumer<YearMonth> consumer){
        for(YearMonth month = from;month.isBefore(to);month = month.plusMonths(1)){
            consumer.accept(month);
        }
    }
}
