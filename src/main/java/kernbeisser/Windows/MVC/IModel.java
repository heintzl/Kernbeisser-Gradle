package kernbeisser.Windows.MVC;

public interface IModel<C extends Controller<? extends IView<C>, ? extends IModel<C>>> {

  default void viewClosed(){}

}
