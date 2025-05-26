package kernbeisser.Enums;

import kernbeisser.Useful.Named;
import lombok.Getter;

public enum AccountingReportGroups implements Named {
  ASSISTED_PURCHASE("Ladendienst"),
  SOLO_PURCHASE("Selbsteinkauf"),
  PAYIN("Guthabeneinzahlungen"),
  REFUND("Sonderzahlungen"),
  OTHER("Andere");

  @Getter private final String name;

  AccountingReportGroups(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
