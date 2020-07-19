package kernbeisser.Enums;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public enum Filters {
  DIGITS(
      new DocumentFilter() {
        @Override
        public void replace(
            FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
          if (!(fb.getDocument().getText(0, fb.getDocument().getLength()).contains(".")
              && text.matches("[,.]"))) {
            fb.replace(offset, length, text.replaceAll("[^\\d]", ""), attrs);
          }
        }
      }),
  DOUBLES(
      new DocumentFilter() {
        @Override
        public void replace(
            FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
          if (!(fb.getDocument().getText(0, fb.getDocument().getLength()).contains(".")
              && text.matches("[,.]"))) {
            fb.replace(
                offset, length, text.replaceAll("[\\D&&[^,.]]", "").replaceAll(",", "."), attrs);
          }
        }
      }),
  PRICES(
      new DocumentFilter() {
        @Override
        public void replace(
            FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
          Document insert = fb.getDocument();
          String current = insert.getText(0, insert.getLength());
          int comma = current.indexOf(".");
          if (comma != -1 && (text.contains(".") || text.contains(","))) {
            return;
          }
          if ((current.length() - comma) > 2) {
            return;
          }
          fb.replace(
              offset, length, text.replaceAll("[\\D&&[^,.]]", "").replaceAll(",", "."), attrs);
        }
      });
  public DocumentFilter documentFilter;

  Filters(DocumentFilter documentFilter) {
    this.documentFilter = documentFilter;
  }
}
