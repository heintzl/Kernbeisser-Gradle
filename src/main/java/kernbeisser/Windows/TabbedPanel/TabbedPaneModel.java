package kernbeisser.Windows.TabbedPanel;

import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.JFrameWindow;

import java.util.ArrayList;
import java.util.List;

public class TabbedPaneModel implements Model<TabbedPaneController> {
    public static TabbedPaneController DEFAULT_TABBED_PANE = new TabbedPaneController() {
        boolean firstTab = true;

        @Override
        public void addTab(Tab tab) {
            super.addTab(tab);
            if (firstTab) {
                DEFAULT_TABBED_PANE.openAsWindow(Window.NEW_VIEW_CONTAINER, (c) -> new JFrameWindow(c) {
                    @Override
                    public void back() {
                        if (DEFAULT_TABBED_PANE.getModel().getTabCount() > 0) {
                            DEFAULT_TABBED_PANE.closeCurrentTab();
                        } else {
                            super.back();
                        }
                    }

                    @Override
                    public boolean commitClose() {
                        if (DEFAULT_TABBED_PANE.getModel().getTabCount() > 0) {
                            DEFAULT_TABBED_PANE.closeCurrentTab();
                            return getModel().getTabCount() == 0;
                        }
                        return true;
                    }
                });
            }
            firstTab = false;
        }
    };

    private final List<Tab> tabs = new ArrayList<>();

    void addTab(Tab tab) {
        tabs.add(tab);
    }

    public void remove(Tab tab) {
        tabs.remove(indexOf(tab));
    }

    public int indexOf(Tab tab) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getController().equals(tab.getController())) {
                return i;
            }
        }
        return -1;
    }

    public Tab getTab(int index) {
        return tabs.get(index);
    }

    public int getTabCount() {
        return tabs.size();
    }

    public int getIndexOfControllerClass(Tab tab) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getController().getClass().equals(tab.getController().getClass())) {
                return i;
            }
        }
        return -1;
    }
}
