package kernbeisser.Useful;

import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;

public interface ActuallyCloneable extends Cloneable {
  public Object clone() throws CloneNotSupportedException;

  public default Object cloneSafe() {
    try {
      return clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(cloneNotSupportedException);
    }
  }
}
