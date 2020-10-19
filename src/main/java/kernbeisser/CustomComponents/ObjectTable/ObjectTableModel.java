package kernbeisser.CustomComponents.ObjectTable;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ObjectTableModel extends DefaultTableModel {

    public ObjectTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public ObjectTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public ObjectTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public ObjectTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);
    }

    public ObjectTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
