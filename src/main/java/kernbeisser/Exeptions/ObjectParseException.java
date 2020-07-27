package kernbeisser.Exeptions;

public class ObjectParseException extends Exception {
  private final String source;
  private final Class to;

  public ObjectParseException(String source, Class to) {
    this.source = source;
    this.to = to;
  }

  @Override
  public String getMessage() {
    return "Cannot parse \"" + source + "\" to " + to.getName();
  }
}
