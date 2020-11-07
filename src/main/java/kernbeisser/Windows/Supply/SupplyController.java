package kernbeisser.Windows.Supply;

import kernbeisser.Windows.MVC.Controller;

public class SupplyController extends Controller<SupplyView, SupplyModel> {

  public SupplyController() {
    super(new SupplyModel());
  }

  @Override
  public void fillView(SupplyView supplyView) {}
}
