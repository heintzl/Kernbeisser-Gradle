package kernbeisser.CustomComponents.AccessChecking;

public interface Predictable<P> {
  boolean isPropertyReadable(P parent);

  boolean isPropertyWriteable(P parent);
}
