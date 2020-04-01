package kernbeisser.Windows.UserInfo;

import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;

public class UserInfoView {

    private JPanel panel1;
    private JTabbedPane tabbedPane;
    private ObjectTable shoppingHistory;
    private ObjectTable valueHistory;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        HistogramDataset dataset = new HistogramDataset();
    }
}
