package kernbeisser.Windows.WindowImpl;

import java.awt.*;
import java.awt.event.WindowEvent;
import javax.swing.*;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowCloseEvent;

public class JFrameWindow extends JFrame implements Window {

  private final IController<?, ?> controller;

  public JFrameWindow(IController<?, ?> controller) {
    this.controller = controller;
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  @Override
  public void setSize(Dimension d) {
    super.setSize(d);
    setLocationRelativeTo(null);
  }

  @Override
  public void addCloseEventListener(WindowCloseEvent windowCloseEvent) {
    addWindowListener(windowCloseEvent);
  }

  @Override
  public void setIcon(Image image) {
    super.setIconImage(image);
  }

  @Override
  public void open() {
    setVisible(true);
  }

  @Override
  public void close() {
    setVisible(false);
  }

  @Override
  public void kill() {
    dispose();
  }

  @Override
  public void simulateCloseEvent() {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  @Override
  public boolean noAccess() {
    JOptionPane.showMessageDialog(this, "Sie haben keine Berechtigung dieses Fenster zu öffen!");
    return false;
  }

  @Override
  public IController<?, ?> getController() {
    return controller;
  }

  @Override
  public void setContent(IController<?, ?> content) {
    add(content.getView().getWrappedContent());
  }

  @Override
  public boolean commitClose() {
    return true;
  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }
}
