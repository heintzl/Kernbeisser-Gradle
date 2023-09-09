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
  private JButton cancel;
  private JButton preview;
  private JCheckBox active;

  @Linked private PostPanelController controller;

  @Override
  public void initialize(PostPanelController controller) {
    editorPane.setVisible(false);
    displayPane.setEditorKit(new HTMLEditorKit());
    displayPane.setEditable(false);
    back.addActionListener(e -> back());
    edit.addActionListener(e -> controller.toggleEditing());
    cancel.addActionListener(e -> cancelEdit());
    preview.setVisible(false);
    preview.addActionListener(e -> previewText());
    active.addActionListener(e -> controller.setActive(active.isSelected()));
    cancel.setVisible(false);
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
    cancel.setVisible(true);
  }

  public void cancelEdit() {
    editorPane.setText(displayPane.getText());
  }

  public void stopEditSession() {
    previewText();
    controller.saveContent(displayPane.getText());
    editorPane.setVisible(false);
    edit.setText("Bearbeiten");
    preview.setVisible(false);
    cancel.setVisible(false);
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
