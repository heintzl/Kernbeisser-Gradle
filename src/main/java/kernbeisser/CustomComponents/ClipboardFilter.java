package kernbeisser.CustomComponents;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.function.Function;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Useful.Tools;

public class ClipboardFilter<T> implements RowFilter<T> {

  Collection<T> filterHits;

  public ClipboardFilter(Function<String[], Collection<T>> clipBoardEntryEvaluator) {
    String clpBoard = getClipboard();
    if (!clpBoard.isEmpty()) {
      filterHits = clipBoardEntryEvaluator.apply(clpBoard.split("\n"));
    }
  }

  public ClipboardFilter(String explainingMessage, Function<String, T> clipBoardEntryEvaluator) {}

  private static String getClipboard() {
    Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    try {
      if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        String text = (String) t.getTransferData(DataFlavor.stringFlavor);
        return text.trim();
      }
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    return "";
  }

  @Override
  public boolean isDisplayed(T t) {
    return (filterHits.isEmpty() || filterHits.contains(t));
  }
}
