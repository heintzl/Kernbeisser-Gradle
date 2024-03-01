package kernbeisser.CustomComponents.ObjectTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.RowFilter;

public class RegexFilter extends RowFilter<Object, Integer> {
  private Matcher matcher;
  private int[] columns;

  private static void checkIndices(int[] columns) {
    for (int i = columns.length - 1; i >= 0; i--) {
      if (columns[i] < 0) {
        throw new IllegalArgumentException("Index must be >= 0");
      }
    }
  }

  @Override
  public boolean include(Entry<?, ? extends Integer> entry) {
    int count = entry.getValueCount();
    if (columns.length > 0) {
      for (int i = columns.length - 1; i >= 0; i--) {
        int index = columns[i];
        if (index < count) {
          if (include(entry, index)) {
            return true;
          }
        }
      }
    } else {
      while (--count >= 0) {
        if (include(entry, count)) {
          return true;
        }
      }
    }
    return false;
  }

  public RegexFilter(Pattern regex, int... columns) {
    checkIndices(columns);
    this.columns = columns;
    if (regex == null) {
      throw new IllegalArgumentException("Pattern must be non-null");
    }
    matcher = regex.matcher("");
  }

  protected boolean include(Entry<? extends Object, ? extends Object> value, int index) {
    matcher.reset(value.getStringValue(index));
    return matcher.find();
  }
}
