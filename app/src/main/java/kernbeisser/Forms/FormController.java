package kernbeisser.Forms;

import kernbeisser.Enums.Mode;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.MVC.IView;
import lombok.Getter;
import lombok.Setter;
import rs.groump.AccessDeniedException;

public abstract class FormController<
        V extends IView<? extends Controller<? extends V, ? extends M>>,
        M extends IModel<? extends Controller<? extends V, ? extends M>>,
        F extends ActuallyCloneable>
    extends Controller<V, M> implements Form<F> {
  @Getter @Setter private Mode mode;

  public FormController(M model) throws AccessDeniedException {
    super(model);
  }
}
