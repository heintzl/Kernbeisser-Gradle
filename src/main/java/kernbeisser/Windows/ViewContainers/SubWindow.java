package kernbeisser.Windows.ViewContainers;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import kernbeisser.Windows.CloseEvent;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainer;

public class SubWindow extends JDialog implements ViewContainer {

  private Controller<?, ?> controller;

  public SubWindow(ViewContainer viewContainer) {
    super(viewContainer.getLoaded().getView().getTopComponent());
    viewContainer
        .getLoaded()
        .addCloseEvent(
            new CloseEvent() {
              @Override
              public boolean shouldClose() {
                return controller.requestClose();
              }

              @Override
              public void closed() {
                controller.notifyClosed();
                SubWindow.this.dispose();
              }
            });
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    super.setLocationRelativeTo(viewContainer.getLoaded().getView().getContent());
  }

  @Override
  public void loadController(Controller<?, ?> controller) {
    this.controller = controller;
    IView<?> view = controller.getView();
    add(view.getContent());
    setTitle(view.getTitle());
    setSize(view.getSize());
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
}
