package kernbeisser.Windows.WindowImpl;

import java.awt.*;
import java.awt.event.WindowEvent;
import javax.swing.*;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowCloseEvent;

public class SubWindow extends JDialog implements Window {

  private final IController<?, ?> controller;

  public SubWindow(IController<?, ?> controller, Window window) {
    super(window.getController().getView().getTopComponent());
    this.controller = controller;
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  @Override
  public void setSize(Dimension d) {
    super.setSize(d);
    super.setLocationRelativeTo(null);
  }

  @Override
  public void addCloseEventListener(WindowCloseEvent runnable) {
    addWindowListener(runnable);
  }

  @Override
  public void simulateCloseEvent() {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  @Override
  public void setIcon(Image image) {
    setIconImage(image);
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
  public void setTitle(String title) {
    super.setTitle(title);
  }
}
