package kernbeisser.Windows.AccountingReports;

import java.util.*;
import java.util.function.Consumer;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Reports.*;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class AccountingReportsModel implements IModel<AccountingReportsController> {

  @Getter private final ExportTypes[] exportTypes = ExportTypes.values();
  @Getter private final List<String> userKeySortOrders = Arrays.asList("Id", "Name");

  public AccountingReportsModel() {}

  public static void exportReport(
      ExportTypes exportType,
      Report report,
      String message,
      Consumer<Throwable> consumeJRException) {
    switch (exportType) {
      case PRINT:
        report.sendToPrinter(message, consumeJRException);
        break;
      case PDF:
        report.exportPdf(message, consumeJRException);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }
}
