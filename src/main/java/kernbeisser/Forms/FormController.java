package kernbeisser.Forms;

import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.MVC.IView;

public abstract class FormController<
        V extends IView<? extends Controller<? extends V, ? extends M>>,
        M extends IModel<? extends Controller<? extends V, ? extends M>>,
        F>
    extends Controller<V, M> implements Form<F> {
  public FormController(M model) throws PermissionKeyRequiredException {
    super(model);
  }
}
