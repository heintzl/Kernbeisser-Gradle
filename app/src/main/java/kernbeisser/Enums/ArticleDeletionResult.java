package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum ArticleDeletionResult implements Named {

  PREORDERED("gerade vorbestellt"),
  RECENTLY_TRADED("Umsatz innerhalb der Karenzzeit"),
  RECENT_INVENTORY("Inventar innerhalb der Karenzzeit"),
  DISCONTINUED("ausgelistet"),
  ERASED("entfernt");
  private final String name;

  ArticleDeletionResult(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public String getName() {
    return null;
  }
}
