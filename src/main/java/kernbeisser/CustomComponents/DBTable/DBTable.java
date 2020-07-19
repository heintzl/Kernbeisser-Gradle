package kernbeisser.CustomComponents.DBTable;

import java.util.Arrays;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBConnection.DBConnection;

public class DBTable<T> extends ObjectTable<T> {
  private String query;
  private int max = -1;

  public DBTable(String query, int max, Collection<Column<T>> columns) {
    super(columns);
    this.query = query;
    this.max = max;
    this.refresh();
  }

  public DBTable(String query, Column<T>... columns) {
    this(query, Arrays.asList(columns));
  }

  public DBTable(String query, Collection<Column<T>> columns) {
    super(columns);
    this.query = query;
    this.refresh();
  }

  public void refresh() {
    clear();
    if (query.equals("")) {
      return;
    }
    EntityManager em = DBConnection.getEntityManager();
    Query dbQuery = em.createQuery(query);
    if (max > 0) {
      dbQuery.setMaxResults(max);
    }
    addAll(dbQuery.getResultList());
    em.close();
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public void setMax(int max) {
    this.max = max;
  }
}
