package kernbeisser.Enums;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public enum Colors {
  LABEL_FOREGROUND() {
    @Override
    public Color getColor() {
      return UIManager.getColor("Label.foreground");
    }
  },
  LABEL_BACKGROUND() {
    @Override
    public Color getColor() {
      return UIManager.getColor("Label.background");
    }
  },
  COMBO_BOX_SELECTION_BACKGROUND() {
    @Override
    public Color getColor() {
      return UIManager.getColor("ComboBox.selectionBackground");
    }
  },
  COMBO_BOX_SELECTION_FOREGROUND() {
    @Override
    public Color getColor() {
      return UIManager.getColor("ComboBox.selectionForeground");
    }
  },

  BACKGROUND_DIRTY() {
    @Override
    public Color getColor() {
      return dirtyBgColor;
    }
  };

  private static final Color dirtyBgColor = new Color(255, 240, 167);

  public Color getColor() {
    return null;
  }

  // keep for getting all currently available color constants
  public static List<String> getallcolors() {
    List<String> colors = new ArrayList<String>();
    for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
      if (entry.getValue() instanceof Color) {
        colors.add((String) entry.getKey()); // all the keys are strings
      }
    }
    Collections.sort(colors);
    return colors;
  }
}
