package kernbeisser.Windows.Tillroll;

import javax.swing.*;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class TillrollView extends JDialog implements IView<TillrollController> {

  private JButton cancel;
  private JComboBox exportType;
  private JButton submit;
  private IntegerParseField days;
  private JPanel main;

  @Linked private TillrollController controller;

  int getDays() {
    return days.getSafeValue();
  }

  ExportTypes getExportType() {
    return (ExportTypes) exportType.getSelectedItem();
  }

  @Override
  public void initialize(TillrollController controller) {
    days.setText("1");
    cancel.addActionListener(e -> back());
    submit.addActionListener(e -> controller.exportTillroll(getExportType(), getDays()));
    ExportTypes[] exportTypes = controller.getExportTypes();
    for (int i = 0; i < exportTypes.length; i++) {
      exportType.addItem(exportTypes[i]);
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void messageNoItems(String title) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Im angegebenen Zeitraum liegen keine Umsätze vor.",
        title,
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageNotImplemented(ExportTypes exportType) {
    JOptionPane.showMessageDialog(
        getContent(),
        exportType.getName() + ": Diese Methode ist noch nicht verfügbar!",
        "Ausgabefehler",
        JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public String getTitle() {
    return "Bonrolle";
  }
}
