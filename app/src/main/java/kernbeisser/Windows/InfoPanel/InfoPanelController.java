package kernbeisser.Windows.InfoPanel;

import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class InfoPanelController extends Controller<InfoPanelView, InfoPanelModel> {

  public InfoPanelController() {
    super(new InfoPanelModel());
  }

  @Override
  public @NotNull InfoPanelModel getModel() {
    return model;
  }

  @Override
  public void fillView(InfoPanelView userInfoView) {}
}
