package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;

public class PreOrderChecklist extends Report {
  private final Collection<PreOrder> preorder;

  public PreOrderChecklist(Collection<PreOrder> preorder) {
    super("preOrderChecklist", "preOrderChecklist" + LocalDate.now().toString());
    this.preorder =
        preorder.stream()
            .filter(p -> !p.getUser().equals(User.getKernbeisserUser()))
            .collect(Collectors.toList());
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
