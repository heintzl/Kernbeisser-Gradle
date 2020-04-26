package kernbeisser.DBEntities;

import java.time.Instant;
import java.time.LocalDate;

public interface ValueChange {
    public User getFrom();
    public User getTo();
    public double getValue();
    public Instant getDate();
    public String getInfo();
}
