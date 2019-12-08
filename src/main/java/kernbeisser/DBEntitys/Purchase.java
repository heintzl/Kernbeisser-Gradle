package kernbeisser.DBEntitys;

import kernbeisser.DBEntitys.SaleSession;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sid;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SaleSession session;

    public int getId() {
        return sid;
    }

    public SaleSession getSession() {
        return session;
    }

    public void setSession(SaleSession session) {
        this.session = session;
    }

    public static List<Purchase> getAll(String condition){
        return Tools.getAll(Purchase.class,condition);
    }
}
