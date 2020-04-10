package kernbeisser.DBEntities;

import java.sql.Date;
import java.time.LocalDate;

public interface ValueChange {
    public User getFrom();
    public User getTo();
    public double getValue();
    public LocalDate getDate();
    public String getInfo();
}
