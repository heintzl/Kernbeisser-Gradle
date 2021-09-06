package kernbeisser.Forms.FormEditor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class FormEditorView<V> implements IView<FormEditorController<V>> {

  private JButton saveButton;
  private JPanel contentPage;
  private JPanel main;
  private JButton cancel;
  private FormEditorController<V> controller;

  public JPanel getContentPage() {
    return contentPage;
  }

  @Override
  public void initialize(FormEditorController<V> controller) {
    this.controller = controller;
    saveButton.addActionListener(actionEvent -> controller.submit());
    cancel.addActionListener(e -> this.back());
  }

  void setMode(Mode mode) {
    switch (mode) {
      case EDIT:
        saveButton.setText("Änderungen speichern");
        saveButton.setIcon(
            IconFontSwing.buildIcon(
                FontAwesome.PENCIL, Tools.scaleWithLabelScalingFactor(15), new Color(0x04A0E8)));
        saveButton.setForeground(new Color(0x04A0E8));
        break;
      case ADD:
        saveButton.setText("Neu Aufnehmen");
        saveButton.setIcon(
            IconFontSwing.buildIcon(
                FontAwesome.CHECK, Tools.scaleWithLabelScalingFactor(15), new Color(0x04E858)));
        saveButton.setForeground(new Color(0x04E858));
        break;
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public @NotNull Dimension getSize() {
    return Tools.floatingSubwindowSize(controller);
  }

  @Override
  public String getTitle() {
    return controller.getModel().getForm().getView().getTitle();
  }

}
