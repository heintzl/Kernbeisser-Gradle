package kernbeisser.DBEntities;

import java.time.Instant;

public interface ValueChange {
    public User getFrom();

    public User getTo();

    public double getValue();

    public Instant getDate();

    public String getInfo();
}
