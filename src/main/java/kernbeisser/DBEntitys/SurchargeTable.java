package kernbeisser.DBEntitys;

import javax.persistence.*;
import java.io.Serializable;

@Table
@Entity
public class SurchargeTable implements Serializable {
    @Id
    @GeneratedValue
    private int stid;

    @Column
    private int surcharge;

    @Column
    private String name;

    public int getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(int surcharge) {
        this.surcharge = surcharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStid() {
        return stid;
    }
}
