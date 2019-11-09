package kernbeisser.DBEntitys;

import kernbeisser.DBEntitys.SaleSession;

import javax.persistence.*;

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
}
