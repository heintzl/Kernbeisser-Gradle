package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;

public class PreOrderChecklist extends Report {
  private final LocalDate deliveryDate;
  private final Collection<PreOrder> preorder;

  public PreOrderChecklist(LocalDate deliveryDate, Collection<PreOrder> preorder) {
    super("preOrderChecklist", "preOrderChecklist" + LocalDate.now().toString());
    this.deliveryDate = deliveryDate;
    this.preorder =
        preorder.stream()
            .filter(p -> !p.getUser().equals(User.getKernbeisserUser()))
            .collect(Collectors.toList());
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("deliveryDate", deliveryDate);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return preorder;
  }
}
