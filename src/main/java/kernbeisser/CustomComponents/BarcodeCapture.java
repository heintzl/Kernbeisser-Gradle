package kernbeisser.CustomComponents;

import kernbeisser.Enums.Setting;
import kernbeisser.Main;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.function.Consumer;

public class BarcodeCapture {

    private boolean isBarcodeInput = false;
    private String barcode = "";
    private Consumer<String> barcodeConsumer;

    public BarcodeCapture(Consumer<String> barcodeConsumer){
        this.barcodeConsumer = barcodeConsumer;
    };

    public boolean processKeyEvent(KeyEvent e) {
        Timer timeoutTimer = new Timer(Setting.SCANNER_TIMEOUT.getIntValue(),t ->
        {
           if (isBarcodeInput) {
               barcode = "";
               isBarcodeInput = false;
               Main.logger.debug("Barcode Scanner timeout");
           }
        });
        if (this.isBarcodeInput) {
            if (e.getKeyCode() == Setting.SCANNER_SUFFIX_KEY.getKeyEventValue()) {
                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    barcodeConsumer.accept(barcode);
                    barcode = "";
                    isBarcodeInput = false;
                }
            } else {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    this.barcode += e.getKeyChar();
                }
            }
            return true;
        } else if (e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getKeyEventValue()) {
            this.isBarcodeInput = true;
            timeoutTimer.start();
            return true;
        } else {
            return false;
        }
    }
}
