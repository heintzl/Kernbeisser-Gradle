package kernbeisser.Reports;

import kernbeisser.DBEntities.PreOrder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class PermissionHolders extends Report {
  private final Collection<PreOrder> preorder;

  public PermissionHolders(Collection<PreOrder> preorder) {
    super("preOrderChecklist", "preOrderChecklist" + LocalDate.now().toString());
    this.preorder = preorder;
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    return preorder;
  }
}
