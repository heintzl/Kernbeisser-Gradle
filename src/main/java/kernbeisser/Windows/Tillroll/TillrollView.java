package kernbeisser.Windows.Tillroll;

import javax.swing.*;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class TillrollView extends JDialog implements IView<TillrollController> {

  private JButton cancel;
  private JComboBox exportType;
  private JButton submit;
  private IntegerParseField days;
  private JPanel main;
  private JRadioButton optBonrolle;
  private JRadioButton optLadendienstEndabrechnung;
  private IntegerParseField startBon;
  private IntegerParseField endBon;

  int getDays() {
    return days.getSafeValue();
  }

  ExportTypes getExportType() {
    return (ExportTypes) exportType.getSelectedItem();
  }

  void submit(TillrollController controller) {
    if (optBonrolle.isSelected()) {
      controller.exportTillroll(getExportType(), getDays());
    } else if (optLadendienstEndabrechnung.isSelected()) {
      controller.exportAccountingReport(
          getExportType(), startBon.getSafeValue(), endBon.getSafeValue());
    }
  }

  @Override
  public void initialize(TillrollController controller) {
    days.setText("1");
    cancel.addActionListener(e -> back());
    submit.addActionListener(e -> submit(controller));
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
}
