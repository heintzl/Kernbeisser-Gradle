package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.TransactionType;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendTransactionTypeEnum implements VersionUpdatingTool {
  @Override
  public void runIntegration() {
    List<String> enumNames = Arrays.stream(TransactionType.values()).map(TransactionType::name).toList();
    this.updateEnum(enumNames, "Transaction", "transactionType");
  }
}
