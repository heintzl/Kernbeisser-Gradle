package kernbeisser.Windows.Supply;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class FileHeader {
  private String id;
  private int runningNumber;

  public static FileHeader parseLine(String line) {
    FileHeader header = new FileHeader();
    header.id = line.substring(2, 14);
    header.runningNumber = Integer.parseInt(line.substring(15));
    return header;
  }
}
