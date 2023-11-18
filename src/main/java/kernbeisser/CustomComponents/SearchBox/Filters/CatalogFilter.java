package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import kernbeisser.DBEntities.CatalogEntry;
import lombok.Setter;

public class CatalogFilter {

  @Setter private boolean filterInactive = false;
  @Setter private boolean filterOutdatedActions = false;
  @Setter private boolean filterOnlyActions = false;

  Runnable callback;

  public CatalogFilter(Runnable refreshMethod) {
    callback = refreshMethod;
  }

  public boolean matches(CatalogEntry e) {
    return (filterInactive || e.isActive())
        && (filterOutdatedActions || !e.isAction() || !e.isOutdatedAction())
        && (!filterOnlyActions || e.isAction());
  }

  public List<JComponent> createFilterUIComponents() {
    List<JComponent> checkBoxes = new ArrayList<>();
    final JCheckBox showOnlyActions = new JCheckBox("nur Aktionen");
    showOnlyActions.addActionListener(
        e -> {
          filterOnlyActions = showOnlyActions.isSelected();
          callback.run();
        });
    checkBoxes.add(showOnlyActions);
    final JCheckBox showInactive = new JCheckBox("mit ausgelisteten");
    showInactive.setSelected(filterInactive);
    checkBoxes.add(showInactive);
    showInactive.addActionListener(
        e -> {
          filterInactive = showInactive.isSelected();
          callback.run();
        });
    showInactive.setSelected(filterInactive);
    checkBoxes.add(showInactive);
    final JCheckBox showOutdatedActions = new JCheckBox("mit abgelaufenen Aktionen");
    showOutdatedActions.addActionListener(
        e -> {
          filterOutdatedActions = showOutdatedActions.isSelected();
          callback.run();
        });
    showOutdatedActions.setSelected(filterOutdatedActions);
    checkBoxes.add(showOutdatedActions);
    return checkBoxes;
  }
}
