package kernbeisser.Windows.TabbedPanel;

import jiconfont.swing.IconFontSwing;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TabbedPaneController implements Controller<TabbedPaneView,TabbedPaneModel> {
    private final TabbedPaneModel model;
    private final TabbedPaneView view;

    public TabbedPaneController() {
        model = new TabbedPaneModel();
        view = new TabbedPaneView();
    }

    @NotNull
    @Override
    public TabbedPaneView getView() {
        return view;
    }

    @NotNull
    @Override
    public TabbedPaneModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
    }

    public void addTab(Tab tab) {
        int posIn = model.getIndexOfControllerClass(tab);
        if (tab.getController().getView().isStackable() || posIn == -1) {
            model.addTab(tab);
            view.addTab(new DefaultTab(IconFontSwing.buildIcon(tab.getIcon(), 20, new Color(0x32C4A2)), tab.getTitle(),
                                       () -> closeTab(tab), () -> view.setSelected(model.indexOf(tab))).getMain(),
                        tab.getController().getView().getWrappedContent(), model.indexOf(tab));
            view.setSelected(model.indexOf(tab));
        } else {
            view.setSelected(posIn);
        }
    }

    public boolean closeCurrentTab() {
        return closeTab(currentTab());
    }

    public Tab currentTab() {
        int index = view.getCurrentTabIndex();
        return model.getTab(index);
    }

    public void unsafeClose(Tab tab) {
        int index = model.getIndexOfControllerClass(tab);
        model.remove(model.getTab(index));
        view.removeTab(index);
    }

    public boolean closeTab(Tab tab) {
        if (tab.commitClose()) {
            if (model.getTabCount() == 0) {
                return false;
            }
            view.removeTab(model.indexOf(tab));
            model.remove(tab);
            return true;
        }
        return false;
    }

    public JFrameWindow openAsWindow() {
        return openAsWindow(Window.NEW_VIEW_CONTAINER, e -> new JFrameWindow(e) {
            @Override
            public void kill() {
                super.kill();
            }

            @Override
            public boolean commitClose() {
                if (getModel().getTabCount() > 0) {
                    closeCurrentTab();
                    return getModel().getTabCount() == 0;
                } else {
                    return true;
                }
            }
        });
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public boolean clear() {
        boolean out = true;
        for (int i = model.getTabCount() - 1; i >= 0; i--) {
            out = out && closeTab(model.getTab(i));
        }
        return out;
    }
}
