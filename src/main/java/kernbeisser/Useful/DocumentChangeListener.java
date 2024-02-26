package kernbeisser.Useful;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface DocumentChangeListener extends DocumentListener {
  void documentChanged(DocumentEvent e);

  @Override
  default void insertUpdate(DocumentEvent e) {
    documentChanged(e);
  }
  ;

  @Override
  default void removeUpdate(DocumentEvent e) {
    documentChanged(e);
  }
  ;
  ;

  @Override
  default void changedUpdate(DocumentEvent e) {
    documentChanged(e);
  }
  ;
  ;
}
