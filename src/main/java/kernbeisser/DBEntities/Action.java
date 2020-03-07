package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Table
@Entity
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int aid;
    @ManyToOne
    @JoinColumn
    private User user;
    @Column
    private Date date;
    @Column
    private String location;
    @Column
    private String action;

    public Action() {
    }

    public Action(User user, Date date, String location, String action) {
        this.user = user;
        this.date = date;
        this.location = location;
        this.action = action;
    }

    Action(User user, String location, String action) {
        this(user, Date.valueOf(LocalDate.now()), location, action);
    }

    Action(User user, String action) {
        this(user, null, action);
    }

    public static List<Action> getAll(String condition) {
        return Tools.getAll(Action.class, condition);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getAid() {
        return aid;
    }

    public User getUser() {
        return user;
    }
}
