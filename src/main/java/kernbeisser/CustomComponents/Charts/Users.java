package kernbeisser.CustomComponents.Charts;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Users {
    public static void main(String[] args) {
        EntityManager em = DBConnection.getEntityManager();
        JFreeChart chart = createBuyChart(em.createQuery("select u from User u", User.class).setMaxResults(1).getSingleResult(), YearMonth.now().minusMonths(12), YearMonth.now());
        XYPlot xyPlot = chart.getXYPlot();
        ValueAxis rangeAxis = xyPlot.getRangeAxis();
        rangeAxis.setRange(0.0, 64);
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame jFrame = new JFrame();
        jFrame.add(chartPanel);
        jFrame.setSize(500,500);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        em.close();

    }

    public static JFreeChart createBuyChart(User user, YearMonth from, YearMonth to){
        EntityManager em = DBConnection.getEntityManager();
        HashMap<YearMonth,Integer> result = new HashMap<>();
        for (Purchase purchase : em.createQuery(
                "select p from Purchase p where p.session.customer.id = :uid and p.createDate between :from and :to",
                Purchase.class)
                                   .setParameter("uid", user.getId())
                                   .setParameter("from", Date.valueOf(from.atDay(1)))
                                   .setParameter("to", Date.valueOf(to.atEndOfMonth())).getResultList()) {

            YearMonth month = YearMonth.from(purchase.getCreateDate().toLocalDate());
            if (result.containsKey(month)) {
                result.replace(month,result.get(month)+1);
            }else {
                result.put(month,1);
            }
        }

        return Graph.generateChart("Einkäufe","Monat","Anzahl",Graph.getYearMonthGraph("",from,to,e -> {
            Integer value = result.get(e);
            if(value==null){
                return 0.;
            }
            return Double.valueOf(value);
        }));
    }

}
