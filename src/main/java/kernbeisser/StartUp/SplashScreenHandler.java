package kernbeisser.StartUp;

import java.awt.*;

public class SplashScreenHandler {

  private final SplashScreen splashScreen;
  private Graphics2D splash;
  private boolean rendered = true;
  private final int x;
  private final int y;
  private final int fontSize;
  private int counter = 0;

  public SplashScreenHandler(int x, int y, int fontSize, Color fontColor) {
    splashScreen = SplashScreen.getSplashScreen();
    this.x = x;
    this.y = y;
    this.fontSize = fontSize;
    if (splashScreen == null) {
      rendered = false;
      return;
    }
    splash = splashScreen.createGraphics();
    splash.setComposite(AlphaComposite.Clear);
    splash.setPaintMode();
    splash.setColor(fontColor);
    splash.setFont(splash.getFont().deriveFont(Font.ITALIC + Font.BOLD, fontSize));
  }

  public void setSplashComment(String comment) {
    if (!rendered) return;
    Dimension size = splashScreen.getSize();
    splash.drawString(comment + " ...", x + counter * 25, y + counter * (fontSize + 2));
    counter += 1;
    splashScreen.update();
  }
}
