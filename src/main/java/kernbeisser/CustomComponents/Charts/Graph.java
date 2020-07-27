package kernbeisser.CustomComponents.Charts;

import java.awt.geom.Point2D;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public interface Graph {
  Iterable<Point2D.Double> getAllPoints();

  String getTitle();

  @NotNull
  @Contract(value = "_, _, _, _ -> new", pure = true)
  static Graph getYearMonthGraph(
      String title, YearMonth from, YearMonth to, Function<YearMonth, Double> value) {
    return new Graph() {
      @Override
      public Iterable<Point2D.Double> getAllPoints() {
        Collection<Point2D.Double> out = new ArrayList<>();
        for (YearMonth month = from; month.isBefore(to); month = month.plusMonths(1)) {
          out.add(new Point2D.Double(month.getMonthValue(), value.apply(month)));
        }
        return out;
      }

      @Override
      public String getTitle() {
        return title;
      }
    };
  }

  static JFreeChart generateChart(String title, String xName, String yName, Graph... graphs) {
    XYSeriesCollection seriesCollection = new XYSeriesCollection();
    double xMin = 0;
    double xMax = 0;
    double yMin = 0;
    double yMax = 0;
    for (Graph graph : graphs) {
      XYSeries xySeries = new XYSeries(graph.getTitle());
      for (Point2D.Double t : graph.getAllPoints()) {
        xMin = Math.min(t.x, xMin);
        xMax = Math.max(t.x, xMax);
        yMin = Math.min(t.y, yMin);
        yMax = Math.max(t.y, yMax);
        xySeries.add(t.x, t.y);
      }
      seriesCollection.addSeries(xySeries);
    }
    JFreeChart jFreeChart = ChartFactory.createXYLineChart(title, xName, yName, seriesCollection);
    XYPlot plot = jFreeChart.getXYPlot();
    plot.getDomainAxis().setRange(xMin - 1, xMax + 1);
    plot.getRangeAxis().setRange(yMin - 1, yMax + 1);
    return jFreeChart;
  }
}
