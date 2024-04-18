package kernbeisser.Windows.PostPanel;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PostPanelView implements IView<PostPanelController> {

  private JPanel main;
  private JSplitPane splitter;
  private JTextPane displayPane;
  private JEditorPane editorPane;
  private JButton back;
  private JButton edit;
  private JButton reset;
  private JButton preview;
  private JCheckBox active;
  private JButton proceed;

  @Linked private PostPanelController controller;

  @Override
  public void initialize(PostPanelController controller) {
    editorPane.setVisible(false);
    displayPane.setEditorKit(new HTMLEditorKit());
    displayPane.setEditable(false);
    back.addActionListener(e -> controller.back(false));
    proceed.addActionListener(e -> controller.back(true));
    edit.addActionListener(e -> controller.toggleEditing());
    reset.addActionListener(e -> reset());
    preview.setVisible(false);
    preview.addActionListener(e -> previewText());
    active.addActionListener(e -> controller.setActive(active.isSelected()));
    reset.setVisible(false);
  }

  public void setHtmlContent(String htmlContent) {
    this.displayPane.setText(htmlContent);
  }

  public void setActive(boolean active) {
    this.active.setSelected(active);
  }

  public void setEditable(boolean isEditable) {
    edit.setEnabled(isEditable);
    edit.setVisible(isEditable);
  }

  public void startEditSession() {
    editorPane.setText(displayPane.getText());
    editorPane.setVisible(true);
    splitter.setDividerLocation(0.3);
    edit.setText("Speichern");
    preview.setVisible(true);
    reset.setVisible(true);
  }

  public void reset() {
    controller.resetContent();
    editorPane.setText(displayPane.getText());
  }

  public void stopEditSession() {
    previewText();
    controller.saveContent(displayPane.getText());
    editorPane.setVisible(false);
    edit.setText("Bearbeiten");
    preview.setVisible(false);
    reset.setVisible(false);
  }

  public void activateConfirmation() {
    back.setText("Zurück");
    proceed.setVisible(true);
  }

  public void setActiveVisible(boolean visible) {
    active.setVisible(visible);
  }

  private void previewText() {
    displayPane.setText(editorPane.getText());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(500, 500);
  }

  @Override
  public String getTitle() {
    return "Info";
  }
}
