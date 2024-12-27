package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import kernbeisser.DBEntities.CatalogEntry;
import lombok.Setter;

public class CatalogFilter {

  @Setter private boolean filterInactive = false;
  @Setter private boolean filterOutdatedOffers = false;
  @Setter private boolean filterOnlyOffers = false;

  Runnable callback;

  public CatalogFilter(Runnable refreshMethod) {
    callback = refreshMethod;
  }

  public boolean matches(CatalogEntry e) {
    return (filterInactive || e.isActive())
        && (filterOutdatedOffers || !e.isOffer() || !e.isOutdatedOffer())
        && (!filterOnlyOffers || e.isOffer());
  }

  public List<JComponent> createFilterUIComponents() {
    List<JComponent> checkBoxes = new ArrayList<>();
    final JCheckBox showOnlyOffers = new JCheckBox("nur Aktionen");
    showOnlyOffers.addActionListener(
        e -> {
          filterOnlyOffers = showOnlyOffers.isSelected();
          callback.run();
        });
    checkBoxes.add(showOnlyOffers);
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
    final JCheckBox showOutdatedOffers = new JCheckBox("mit abgelaufenen Aktionen");
    showOutdatedOffers.addActionListener(
        e -> {
          filterOutdatedOffers = showOutdatedOffers.isSelected();
          callback.run();
        });
    showOutdatedOffers.setSelected(filterOutdatedOffers);
    checkBoxes.add(showOutdatedOffers);
    return checkBoxes;
  }
}
