package kernbeisser.CustomComponents.ComboBox;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiPredicate;
import javax.swing.*;
import kernbeisser.Enums.Setting;

public class AdvancedComboBoxKeySelectionManager<T> implements JComboBox.KeySelectionManager {

  private final BiPredicate<T, String> displayFilter;

  private int lastSelection;

  private Instant lastKey = Instant.now();

  private StringBuilder sb = new StringBuilder();

  AdvancedComboBoxKeySelectionManager(BiPredicate<T, String> display) {
    this.displayFilter = display;
  }

  @Override
  public int selectionForKey(char aKey, ComboBoxModel aModel) {
    if (Duration.between(lastKey, Instant.now()).toMillis()
        > Setting.COMBO_BOX_SEARCH_TIMEOUT.getIntValue()) {
      sb = new StringBuilder();
    }
    sb.append(aKey);
    lastKey = Instant.now();
    for (int i = 0; i < aModel.getSize(); i++) {
      if (displayFilter.test((T) aModel.getElementAt(i), sb.toString())) {
        lastSelection = i;
        return i;
      }
    }
    return lastSelection;
  }
}
