package kernbeisser.Windows.ViewContainers;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainer;
import org.jetbrains.annotations.NotNull;

public class SubWindow extends JDialog implements ViewContainer {

  private Controller<?, ?> controller;
  private final ViewContainer parent;
  private boolean requiresCloseListener = true;
  private Dimension size = null;

  public SubWindow(@NotNull ViewContainer viewContainer) {
    super(viewContainer.getLoaded().getView().getTopComponent(), ModalityType.APPLICATION_MODAL);
    this.parent = viewContainer;
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            if (controller.requestClose()) {
              controller.notifyClosed();
              kill();
            }
          }
        });
    requiresCloseListener = false;
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  public SubWindow withSize(Dimension size) {
    this.size = size;
    return this;
  }

  @Override
  public void loadController(@NotNull Controller<?, ?> controller) {
    this.controller = controller;
    IView<?> view = controller.getView();
    add(view.getContent());
    setTitle(view.getTitle());
    setSize(size == null ? Tools.floatingSubwindowSize(controller) : size);
    setLocationRelativeTo(parent.getLoaded().getView().getTopComponent());
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    if (requiresCloseListener) {
      addWindowListener(
          new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
              if (controller.requestClose()) {
                controller.notifyClosed();
                dispose();
              }
            }
          });
    }
    setVisible(true);
  }

  @Override
  public Controller<?, ?> getLoaded() {
    return controller;
  }

  @Override
  public void requestClose() {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  @Override
  public void kill() {
    dispose();
  }
}
