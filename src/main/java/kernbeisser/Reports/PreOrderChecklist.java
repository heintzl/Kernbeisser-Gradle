package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import kernbeisser.DBEntities.PreOrder;

public class PreOrderChecklist extends Report {
  private final Collection<PreOrder> preorder;

  public PreOrderChecklist(Collection<PreOrder> preorder) {
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
