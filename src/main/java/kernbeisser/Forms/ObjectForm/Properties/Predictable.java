package kernbeisser.Forms.ObjectForm.Properties;

public interface Predictable<P> {
  boolean isPropertyReadable(P parent);

  boolean isPropertyWriteable(P parent);
}
