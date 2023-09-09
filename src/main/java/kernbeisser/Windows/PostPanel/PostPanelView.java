package kernbeisser.Windows.PostPanel;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PostPanelView implements IView<PostPanelController> {

  private JPanel main;
  private JTextPane contentText;
  private JButton back;
  private JButton edit;

  private JButton cancel;

  @Linked private PostPanelController controller;

  @Override
  public void initialize(PostPanelController controller) {
    contentText.setEditorKit(new HTMLEditorKit());
    contentText.setEditable(false);
    back.addActionListener(e -> back());
    edit.addActionListener(e -> controller.toggleEditing());
    cancel.addActionListener(e -> back());
  }

  public void setHtmlContent(String htmlContent) {
    this.contentText.setText(htmlContent);
  }

  public void setEditable(boolean isEditable) {
    edit.setEnabled(isEditable);
    edit.setVisible(isEditable);
  }

  public void startEditSession() {
    contentText.setEditable(true);
    edit.setText("Speichern");
  }

  public void stopEditSession() {
    controller.saveContent(contentText.getText());
    contentText.setEditable(false);
    edit.setText("Bearbeiten");
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
