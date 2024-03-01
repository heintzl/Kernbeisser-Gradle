package kernbeisser.Tasks.DTO;

import java.util.Objects;
import lombok.Getter;

@Getter
public class KornkraftGroup {
  private long position;
  private String inetwg;
  private String itext;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KornkraftGroup that = (KornkraftGroup) o;
    return position == that.position;
  }

  @Override
  public int hashCode() {
    return Objects.hash(position);
  }
}
