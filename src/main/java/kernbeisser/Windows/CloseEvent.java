package kernbeisser.Windows;

public interface CloseEvent {
  default boolean shouldClose() {
    return true;
  }

  void closed();
}
