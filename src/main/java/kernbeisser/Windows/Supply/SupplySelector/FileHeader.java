package kernbeisser.Windows.Supply.SupplySelector;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class FileHeader {
  private String id;
  private int orderNr;
  private int orderType;

  public static FileHeader parseLine(String line) {
    FileHeader header = new FileHeader();
    header.id = line.substring(2, 14);
    header.orderNr = Integer.parseInt(line.substring(15, 21));
    header.orderType = Integer.parseInt(String.valueOf(line.charAt(21)));
    return header;
  }
}
