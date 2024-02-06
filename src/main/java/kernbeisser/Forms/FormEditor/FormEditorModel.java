package kernbeisser.Forms.FormEditor;

import kernbeisser.Forms.FormController;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class FormEditorModel<V extends ActuallyCloneable>
    implements IModel<FormEditorController<V>> {

  private final FormController<?, ?, V> form;
  private final Runnable submit;

  public FormEditorModel(FormController<?, ?, V> form, Runnable submit) {
    this.form = form;
    this.submit = submit;
  }
}
