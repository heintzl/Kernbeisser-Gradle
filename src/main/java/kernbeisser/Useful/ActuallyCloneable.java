package kernbeisser.Useful;

public interface ActuallyCloneable extends Cloneable {
  public Object clone() throws CloneNotSupportedException;

  public default Object cloneSafe() {
    try {
      return clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw Tools.showUnexpectedErrorWarning(cloneNotSupportedException);
    }
  }
}
