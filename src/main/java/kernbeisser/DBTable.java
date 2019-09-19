package kernbeisser;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.List;

public class DBTable extends JTable {
    private String query;
    private int max = -1;
    Field[] fields;
    public DBTable(String query, int max, Field... fields){
        this.query=query;
        this.max=max;
        this.fields=fields;
        refresh();
    }
    DBTable(String query,int max){
        this.query=query;
        this.max=max;
        refresh();
    }
    public void refresh(){
        EntityManager em = DBConnection.getEntityManager();
        Query dbQuery = em.createQuery(query);
        if(max>0)
            dbQuery.setMaxResults(max);
        List result = dbQuery.getResultList();
        Object[][] values = new Object[result.size()][fields.length];
        for (int i = 0; i < result.size(); i++) {
            for (int c = 0; c < fields.length; c++) {
                try {
                    fields[c].setAccessible(true);
                    values[i][c]=fields[c].get(result.get(i));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        em.close();
        setModel(new DefaultTableModel(values, Tools.transform(fields,String.class, Field::getName)));
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public void setMax(int max) {
        this.max = max;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }
}
