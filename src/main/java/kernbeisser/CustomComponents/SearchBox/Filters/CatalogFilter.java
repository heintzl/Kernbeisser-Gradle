package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import kernbeisser.DBEntities.CatalogEntry;
import lombok.Setter;

public class CatalogFilter {

  @Setter private boolean filterShowInactive = false;
  @Setter private boolean filterShowOutdatedActions = false;

  Runnable callback;

  public CatalogFilter(Runnable refreshMethod) {
    callback = refreshMethod;
  }

  public boolean matches(CatalogEntry e) {
    return (filterShowInactive || !"|X|V|".contains(e.getAenderungskennung()))
        && (filterShowOutdatedActions || !e.isAction() || !e.isOutdatedAction());
  }

  public List<JComponent> createFilterUIComponents() {
    List<JComponent> checkBoxes = new ArrayList<>();
    final JCheckBox showInactive = new JCheckBox("mit ausgelisteten");
    showInactive.addActionListener(
        e -> {
          filterShowInactive = showInactive.isSelected();
          callback.run();
        });
    showInactive.setSelected(filterShowInactive);
    checkBoxes.add(showInactive);
    final JCheckBox showOutdatedActions = new JCheckBox("mit abgelaufenen Aktionen");
    showOutdatedActions.addActionListener(
        e -> {
          filterShowOutdatedActions = showOutdatedActions.isSelected();
          callback.run();
        });
    showOutdatedActions.setSelected(filterShowOutdatedActions);
    checkBoxes.add(showOutdatedActions);
    return checkBoxes;
  }
}
