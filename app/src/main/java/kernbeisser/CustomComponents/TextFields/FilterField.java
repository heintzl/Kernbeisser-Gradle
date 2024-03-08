package kernbeisser.CustomComponents.TextFields;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import kernbeisser.Exeptions.IncorrectInput;
import org.jetbrains.annotations.NotNull;

public class FilterField<T> extends PermissionField {
  private final Transformable<T> transformer;

  FilterField(@NotNull Transformable<T> transformer, boolean allowWrongInput) {
    this.transformer = transformer;
    ((AbstractDocument) getDocument())
        .setDocumentFilter(
            new DocumentFilter() {
              @Override
              public void replace(
                  FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                  throws BadLocationException {
                String before = getText();
                fb.replace(offset, length, text, attrs);
                if (!allowWrongInput) {
                  if (!isValidInput()) {
                    setText(before);
                  }
                }
              }

              @Override
              public void remove(FilterBypass fb, int offset, int length)
                  throws BadLocationException {
                String before = getText();
                fb.remove(offset, length);
                if (!allowWrongInput) {
                  if (!isValidInput()) {
                    setText(before);
                  }
                }
              }
            });
  }

  FilterField(Transformable<T> transformer) {
    this(transformer, false);
  }

  private boolean isValidInput() {
    try {
      transformer.toObject(getText());
      return true;
    } catch (IncorrectInput incorrectInput) {
      return false;
    }
  }

  public T getValue() throws IncorrectInput {
    return transformer.toObject(getText());
  }

  public T getSafeValue() {
    try {
      return getValue();
    } catch (IncorrectInput incorrectInput) {
      throw new RuntimeException(incorrectInput);
    }
  }
}
