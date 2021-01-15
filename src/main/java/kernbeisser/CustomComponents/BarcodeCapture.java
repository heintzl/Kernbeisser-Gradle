package kernbeisser.CustomComponents;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import lombok.Getter;

public class BarcodeCapture {

  private boolean currentlyCapturingBarcode = false;
  private final StringBuilder barcode = new StringBuilder();
  @Getter private final Consumer<String> barcodeConsumer;
  private final Timer timeoutTimer =
      new Timer(
          Setting.SCANNER_TIMEOUT.getIntValue(),
          t -> {
            if (currentlyCapturingBarcode) {
              String message =
                  barcode.toString().equals("")
                      ? "Bitte Barcode eingeben:"
                      : "Ein Barcode wurde empfangen, ohne dass die Übertragung beendet wurde. Er kann hier bearbeitet und übernommen werden:";
              String response = barcodeManInput(message, barcode);
              if (response != null) {
                try {
                  getBarcodeConsumer().accept(response);
                } catch (Throwable e) {
                  stopChaining();
                  Main.logger.error("Barcode cmd execution failed");
                  Tools.showUnexpectedErrorWarning(e);
                }
              }
              stopChaining();
              Main.logger.debug("Barcode Scanner timeout");
            }
          });

  private String barcodeManInput(String message, StringBuilder initValue) {
    return (String)
        JOptionPane.showInputDialog(
            null,
            message,
            "Barcode Eingabe",
            JOptionPane.INFORMATION_MESSAGE,
            IconFontSwing.buildIcon(FontAwesome.BARCODE, 64),
            null,
            initValue.toString());
  }

  public BarcodeCapture(Consumer<String> barcodeConsumer) {
    this.barcodeConsumer = barcodeConsumer;
  }

  public void startChaining() {
    currentlyCapturingBarcode = true;
    timeoutTimer.start();
  }

  public void stopChaining() {
    timeoutTimer.stop();
    barcode.setLength(0);
    currentlyCapturingBarcode = false;
  }

  public boolean processKeyEvent(KeyEvent e) {
    if (this.currentlyCapturingBarcode) {
      timeoutTimer.restart();
      if (e.getKeyCode() == Setting.SCANNER_SUFFIX_KEY.getKeyEventValue()
          && e.getID() == KeyEvent.KEY_RELEASED) {
        timeoutTimer.stop();
        barcodeConsumer.accept(barcode.toString());
        stopChaining();
      } else if (e.getID() == KeyEvent.KEY_TYPED) {
        barcode.append(e.getKeyChar());
      }
      return true;
    } else if (e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getKeyEventValue()) {
      startChaining();
      return true;
    } else {
      return false;
    }
  }
}
