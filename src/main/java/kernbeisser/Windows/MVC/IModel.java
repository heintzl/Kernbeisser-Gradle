package kernbeisser.Windows.MVC;

public interface IModel<C extends IController<? extends IView<C>, ? extends IModel<C>>> {}
