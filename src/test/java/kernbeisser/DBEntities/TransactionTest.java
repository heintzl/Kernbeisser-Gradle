package kernbeisser.DBEntities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TransactionTest {

  public static boolean isValidTransaction(
      double txValue,
      double fromBalance,
      double toBalance,
      boolean fromCanGoUnderMin,
      boolean toCanGoUnderMin) {
    double globalBalanceMinimum = 0.0;
    double remainingFromSideBalanceAfterTx = fromBalance - txValue;
    if (remainingFromSideBalanceAfterTx < globalBalanceMinimum) {
      return fromCanGoUnderMin;
    }
    // TODO should this be + to ever become valid if we have a negative tx?
    double remainingToSideBalanceAfterTx = toBalance - txValue;
    boolean txValueNegative = txValue < 0;
    if (txValueNegative && remainingToSideBalanceAfterTx < globalBalanceMinimum) {
      return toCanGoUnderMin;
    }
    return true;
  }

  @ParameterizedTest
  @CsvSource(
      value = {"1.0:2.0:0.0:true:true:true"},
      delimiter = ':')
  void isValidTransaction(
      double txValue,
      double fromBalance,
      double toBalance,
      boolean fromCanGoUnderMin,
      boolean toCanGoUnderMin,
      boolean expectedResult) {
    assertEquals(
        expectedResult,
        isValidTransaction(txValue, fromBalance, toBalance, fromCanGoUnderMin, toCanGoUnderMin));
  }
}
