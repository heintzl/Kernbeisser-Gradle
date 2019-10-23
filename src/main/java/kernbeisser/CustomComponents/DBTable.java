package kernbeisser.CustomComponents;

import kernbeisser.DBConnection;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.lang.reflect.Field;

public class DBTable<T> extends ObjectTable<T> {
    private String query;
    private int max = -1;
    Field[] fields;
    public DBTable(String query, int max, Field... fields){
        super(fields);
        this.query=query;
        this.max=max;
        this.fields=fields;
        this.refresh();
    }
    public DBTable(String query, Field... fields){
        super(fields);
        this.query=query;
        this.fields=fields;
        this.refresh();
    }
    public void refresh(){
        if(query.equals(""))return;
        EntityManager em = DBConnection.getEntityManager();
        Query dbQuery = em.createQuery(query);
        if(max>0)
            dbQuery.setMaxResults(max);
        addAll(dbQuery.getResultList());
        em.close();
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public void setFields(Field ... fields) {
        this.fields = fields;
    }
}
