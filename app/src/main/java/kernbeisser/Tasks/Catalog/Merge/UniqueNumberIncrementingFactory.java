package kernbeisser.Tasks.Catalog.Merge;

import java.util.Set;

public class UniqueNumberIncrementingFactory {
  private final Set<Integer> taken;
  private int offset;

  public UniqueNumberIncrementingFactory(Set<Integer> taken, int offset) {
    this.taken = taken;
    this.offset = offset;
  }

  public int reserveNextFreeNumber() {
    while (true) {
      if (taken.add(offset++)) {
        return offset - 1;
      }
    }
  }
}
