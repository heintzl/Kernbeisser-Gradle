package kernbeisser.VersionIntegrationTools.UpdatingTools;

import java.util.Arrays;
import java.util.List;
import kernbeisser.Enums.TransactionType;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;

public class ExtendTransactionTypeEnum implements VersionUpdatingTool {
  @Override
  public void runIntegration() {
    List<String> enumNames =
        Arrays.stream(TransactionType.values()).map(TransactionType::name).toList();
    this.updateEnum(enumNames, "Transaction", "transactionType");
  }
}
