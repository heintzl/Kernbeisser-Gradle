package kernbeisser.Reports;

import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PreOrderChecklist extends Report {
  private final LocalDate deliveryDate;
  private final Collection<PreOrder> preorder;

  public PreOrderChecklist(LocalDate deliveryDate, Collection<PreOrder> preorder) {
    super(ReportFileNames.PREORDER_CHECKLIST_REPORT_FILENAME);
    this.deliveryDate = deliveryDate;
    this.preorder =
            preorder.stream()
                    .filter(p -> !p.getUser().equals(User.getKernbeisserUser()))
                    .sorted(Comparator.comparing(p -> p.getUser().getFullName(true)))
                    .collect(Collectors.toList());
  }

  @Override
  String createOutFileName() {
    return "preOrderChecklist" + LocalDate.now();
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
