package kernbeisser.CustomComponents;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Windows.MVC.IView;

public class ViewMainPanel extends JPanel {

  private final IView<?> view;

  public ViewMainPanel(JComponent mainPanel, IView<?> view) {
    super(new GridLayout(1, 1));
    super.add(mainPanel);
    this.view = view;
  }

  public IView<?> getView() {
    return view;
  }
}
