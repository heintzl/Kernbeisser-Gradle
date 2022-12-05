package kernbeisser.Reports;

import kernbeisser.Useful.Named;

public enum UserNameObfuscation implements Named {
  FULL("nur Ids"),
  WITHOUTPAYIN("Namen bei Konto-Einzahlung"),
  NONE("nur Namen");

  private final String name;

  UserNameObfuscation(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String toString() {
    return name;
  }
}
