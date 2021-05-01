package kernbeisser.Windows.EditUsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.DBEntities.User;

public class UsersFilter {

  static final int FILTER_ALL = 0;
  static final int FILTER_ACTIVE = 1;
  static final int FILTER_INACTIVE = 2;
  static final int FILTER_BEGINNERS = 3;

  int userFilter;

  Runnable callback;

  public UsersFilter(Runnable refreshMethod, int initFilterState) {
    callback = refreshMethod;
    userFilter = initFilterState;
  }

  public Collection<User> searchable(String s, int max) {
    Predicate<User> filter;
    switch (userFilter) {
      case FILTER_ACTIVE:
        filter = User::isActive;
        break;
      case FILTER_INACTIVE:
        filter = (u -> !u.isActive());
        break;
      case FILTER_BEGINNERS:
        filter = User::isBeginner;
        break;
      default:
        filter = (u -> true);
    }

    return User.defaultSearch(s, max).stream().filter(filter).collect(Collectors.toList());
  }

  private void setFilterState(int state) {
    userFilter = state;
    callback.run();
  }

  public List<JRadioButton> createFilterOptionButtons() {
    ButtonGroup filterGroup = new ButtonGroup();
    List<JRadioButton> filterButtons = new ArrayList<>();
    JRadioButton filterButton = new JRadioButton("nur Aktive");
    filterButton.addActionListener(e -> setFilterState(FILTER_ACTIVE));
    filterButton.setSelected(true);
    filterGroup.add(filterButton);
    filterButtons.add(filterButton);
    filterButton = new JRadioButton("nur Inaktive");
    filterButton.addActionListener(e -> setFilterState(FILTER_INACTIVE));
    filterGroup.add(filterButton);
    filterButtons.add(filterButton);
    filterButton = new JRadioButton("Probemitglieder");
    filterButton.addActionListener(e -> setFilterState(FILTER_BEGINNERS));
    filterGroup.add(filterButton);
    filterButtons.add(filterButton);
    filterButton = new JRadioButton("Alle");
    filterButton.addActionListener(e -> setFilterState(FILTER_ALL));
    filterGroup.add(filterButton);
    filterButtons.add(filterButton);
    return filterButtons;
  }
}
