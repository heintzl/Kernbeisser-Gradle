package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
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
    private String location;
    @Column
    private String action;
    @CreationTimestamp
    private Instant createTime;

    public static List<Action> getAll(String condition) {
        return Tools.getAll(Action.class, condition);
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

    public static void doAction(String location,String action,User user){
        Action a = new Action();
        a.location = location;
        a.action = action;
        a.user = user;
        Tools.runInSession(e -> e.persist(a));
    }

    public static void logCurrentFunctionCall(User user){
        StackTraceElement superFunction = Thread.currentThread().getStackTrace()[3];
        doAction(superFunction.getClassName(),superFunction.getMethodName(),user);
    }

    public static void logCurrentFunctionCall(){
        logCurrentFunctionCall(LogInModel.getLoggedIn());
    }

    public String getLocation() {
        return location;
    }

    public Instant getCreateTime() {
        return createTime;
    }
}
