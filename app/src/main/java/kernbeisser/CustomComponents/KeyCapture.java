package kernbeisser.CustomComponents;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.function.Consumer;

public class KeyCapture {

  HashMap<Integer, Runnable> keyFunctions = new HashMap<Integer, Runnable>();

  public boolean processKeyEvent(KeyEvent e) {
    int key = e.getKeyCode() * (e.isAltDown() ? 1024 : e.isControlDown() ? 2048 : 1);
    boolean isMappedKey = keyFunctions.containsKey(key);
    if (isMappedKey && e.getID() == KeyEvent.KEY_RELEASED) {
      keyFunctions.get(key).run();
    }
    return isMappedKey;
  }

  public void addF2ToF8NumberActions(Consumer<String> fnKeyAction) {
    add(KeyEvent.VK_F2, () -> fnKeyAction.accept("2"));
    add(KeyEvent.VK_F3, () -> fnKeyAction.accept("3"));
    add(KeyEvent.VK_F4, () -> fnKeyAction.accept("4"));
    add(KeyEvent.VK_F5, () -> fnKeyAction.accept("5"));
    add(KeyEvent.VK_F6, () -> fnKeyAction.accept("6"));
    add(KeyEvent.VK_F7, () -> fnKeyAction.accept("8"));
    add(KeyEvent.VK_F8, () -> fnKeyAction.accept("10"));
  }

  public void add(int key, Runnable cmd) {
    keyFunctions.put(key, cmd);
  }

  public void addALT(int key, Runnable cmd) {
    keyFunctions.put(key * 1024, cmd);
  }

  public void addCTRL(int key, Runnable cmd) {
    keyFunctions.put(key * 2048, cmd);
  }

  public void remove(int key) {
    keyFunctions.remove(key);
  }

  public void clear() {
    keyFunctions.clear();
  }
}
