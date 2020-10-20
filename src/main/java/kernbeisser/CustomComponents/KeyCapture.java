package kernbeisser.CustomComponents;

import java.awt.event.KeyEvent;
import java.util.HashMap;

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
