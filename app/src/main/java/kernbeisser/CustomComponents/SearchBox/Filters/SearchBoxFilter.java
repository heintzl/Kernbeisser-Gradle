package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.Collection;
import java.util.List;
import javax.swing.*;

public interface SearchBoxFilter<T> {
  Collection<T> searchable(String s, int max);

  List<JComponent> createFilterUIComponents();
}
