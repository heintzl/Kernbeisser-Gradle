package kernbeisser.CustomComponents;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.function.Function;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Dialogs.RememberDialog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;

public class ClipboardFilter<T> implements RowFilter<T> {

  private final Collection<T> filterHits;

  public ClipboardFilter(Function<String[], Collection<T>> clipBoardEntryEvaluator) {
    this(clipBoardEntryEvaluator, "", "");
  }

  public ClipboardFilter(
      Function<String[], Collection<T>> clipBoardEntryEvaluator,
      String explainingMessage,
      String savedDialogName) {
    if (!explainingMessage.isEmpty()) {
      RememberDialog.showDialog(
          LogInModel.getLoggedIn(),
          "clpBoardFilterPermissionAssignment",
          null,
          explainingMessage,
          "Filterung auf Zwischenablage");
    }
    String clpBoard = getClipboard();
    filterHits = clipBoardEntryEvaluator.apply(clpBoard.split("\n"));
    if (!isFiltered()) {
      JOptionPane.showMessageDialog(
          null,
          "Die Zwischenablage enthält anscheinend keine passenden Informationen!",
          "Filterung auf Zwischenablage",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  public boolean isFiltered() {
    return !filterHits.isEmpty();
  }

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
