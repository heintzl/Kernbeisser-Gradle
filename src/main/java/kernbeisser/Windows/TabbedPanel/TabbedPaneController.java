package kernbeisser.Windows.TabbedPanel;

import jiconfont.swing.IconFontSwing;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TabbedPaneController implements Controller<TabbedPaneView,TabbedPaneModel> {
    private final TabbedPaneModel model;
    private final TabbedPaneView view;
    public TabbedPaneController(){
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
    public void fillUI() { }

    public void addTab(Tab tab){
        int posIn = model.getIndexOfControllerClass(tab);
        if(posIn==-1) {
            model.addTab(tab);
            view.addTab(new DefaultTab(IconFontSwing.buildIcon(tab.getIcon(), 20, new Color(0x32C4A2)), tab.getTitle(),
                                       () -> closeTab(tab), () -> view.setSelected(model.indexOf(tab))).getMain(),
                        tab.getController().getView().getContent(), model.indexOf(tab));
            view.setSelected(model.indexOf(tab));
        }else {
            view.setSelected(posIn);
        }
    }

    public void closeCurrentTab(){
        int index = view.getCurrentTabIndex();
        closeTab(model.getTab(index));
    }

    public void closeTab(Tab tab){
        if (tab.commitClose()) {
            if(model.getTabCount()==1)return;
            view.removeTab(model.indexOf(tab));
            model.remove(tab);
        }
    }

    public JFrameWindow openAsWindow(){
        return openAsWindow(Window.NEW_VIEW_CONTAINER, e -> new JFrameWindow(e){
            @Override
            public void back() {
                if (getModel().getTabCount() > 0) {
                    closeCurrentTab();
                }
                super.back();
            }
        });
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void clear() {
        for (int i = model.getTabCount() - 1; 0 <= i; i--) {
            model.remove(model.getTab(i));
            view.removeTab(i);
        }
    }
}
