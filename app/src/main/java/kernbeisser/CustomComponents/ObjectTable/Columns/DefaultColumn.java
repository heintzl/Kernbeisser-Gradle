package kernbeisser.CustomComponents.ObjectTable.Columns;

import kernbeisser.CustomComponents.ObjectTable.Column;
import lombok.Setter;

public abstract class DefaultColumn<T> implements Column<T> {

  private final String name;
  @Setter private boolean usesStandardFiler = false;

  protected DefaultColumn(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean usesStandardFilter() {
    return usesStandardFiler;
  }
}
