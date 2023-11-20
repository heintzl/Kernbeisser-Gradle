package kernbeisser.Windows.Supply.SupplySelector;

import lombok.Getter;

@Getter
public class LineError {
  private final int lineNumber;
  private final String line;
  private final Exception exception;

  public LineError(int lineNumber, String line, Exception e) {
    this.lineNumber = lineNumber;
    this.line = line;
    this.exception = e;
  }
}
