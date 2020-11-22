package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;

public class PreOrderChecklist extends Report {
  public PreOrderChecklist() {
    super("preOrderChecklist", "preOrderChecklist" + LocalDate.now().toString());
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    return PreOrder.getAll("where user_id <> " + User.getKernbeisserUser().getId());
  }
}
