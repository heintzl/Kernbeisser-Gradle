package kernbeisser.Export;

import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.PreOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public class OrderWithAlternative {

  @Getter private final CatalogEntry orderEntry;
  @Getter private final CatalogEntry alternativeEntry;

  private OrderWithAlternative(
      @NotNull CatalogEntry orderEntry, @Nullable CatalogEntry alternativeEntry) {
    this.orderEntry = orderEntry;
    this.alternativeEntry = alternativeEntry;
  }

  static OrderWithAlternative of(PreOrder preOrder) {
    return new OrderWithAlternative(
        preOrder.getCatalogEntry(), preOrder.getAlternativeCatalogEntry());
  }
}
