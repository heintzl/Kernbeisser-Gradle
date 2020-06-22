package kernbeisser.CustomComponents;

import kernbeisser.Enums.Setting;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class BarcodeCapture {

    private boolean isBarcodeInput = false;
    private String barcode = "";
    private Consumer<String> barcodeConsumer;

    public BarcodeCapture(Consumer<String> barcodeConsumer){
        this.barcodeConsumer = barcodeConsumer;
    };

    public boolean processKeyEvent(KeyEvent e) {
        if (this.isBarcodeInput) {
            if (e.getKeyCode() == Setting.SCANNER_SUFFIX_KEY.getKeyEventValue()) {
                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    barcodeConsumer.accept(barcode);
                    this.barcode = "";
                    this.isBarcodeInput = false;
                }
            } else {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    this.barcode += e.getKeyChar();
                }
            }
            return true;
        } else if (e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getKeyEventValue()) {
            this.isBarcodeInput = true;
            return true;
        } else {
            return false;
        }
    }
}
