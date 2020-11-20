package kernbeisser.CustomComponents.AccessChecking;

import javax.swing.JLabel;

public class AccessCheckingLabel<T> extends JLabel implements Bounded<T, String> {

  @Override
  public void inputChanged() {}

  private final Getter<T, String> getter;

  public AccessCheckingLabel(Getter<T, String> getter) {
    this.getter = getter;
  }

  @Override
  public boolean isInputChanged() {
    return false;
  }

  @Override
  public void setObjectData(T data) {
    setText(getter.get(data));
  }

  @Override
  public void writeInto(T p) {}

  @Override
  public void markWrongInput() {}

  @Override
  public Getter<T, String> getGetter() {
    return getter;
  }

  @Override
  // ignored
  public Setter<T, String> getSetter() {
    return (a, b) -> {};
  }

  private String original;

  @Override
  public void setReadable(boolean b) {
    super.setText(b ? original : "[Keine Leseberechtigung]");
  }

  @Override
  public void setText(String text) {
    original = text;
  }

  @Override
  public void setWriteable(boolean b) {}

  @Override
  public boolean validInput() {
    return false;
  }
}
