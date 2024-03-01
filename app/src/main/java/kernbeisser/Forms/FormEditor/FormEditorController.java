package kernbeisser.Forms.FormEditor;

import java.awt.GridLayout;
import java.util.concurrent.atomic.AtomicReference;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormController;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Windows.MVC.Controller;
import rs.groump.AccessDeniedException;

public class FormEditorController<V extends ActuallyCloneable>
    extends Controller<FormEditorView<V>, FormEditorModel<V>> {

  public FormEditorController(FormController<?, ?, V> form, Runnable submit)
      throws AccessDeniedException {
    super(new FormEditorModel<>(form, submit));
  }

  @Override
  public void fillView(FormEditorView<V> formEditorView) {
    formEditorView.getContentPage().setLayout(new GridLayout(1, 1));
    formEditorView.getContentPage().add(model.getForm().getView().getContent());
  }

  public void setMode(Mode mode) {
    getView().setMode(mode);
  }

  public void submit() {
    getModel().getSubmit().run();
  }

  public static <T extends ActuallyCloneable> FormEditorController<T> create(
      T source, FormController<?, ?, T> controller, Mode mode) {
    controller.getObjectContainer().setSource(source);
    controller.setMode(mode);
    AtomicReference<FormEditorController<T>> controllerAtomicReference = new AtomicReference<>();
    controllerAtomicReference.set(
        new FormEditorController<>(
            controller,
            () -> {
              if (controller.getObjectContainer().applyMode(mode)) {
                controllerAtomicReference.get().getView().back();
              }
            }));
    return controllerAtomicReference.get();
  }
}
