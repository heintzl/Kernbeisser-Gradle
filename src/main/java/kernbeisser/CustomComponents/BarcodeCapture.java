package kernbeisser.CustomComponents;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.function.Consumer;

import static jiconfont.icons.font_awesome.FontAwesome.BARCODE;

public class BarcodeCapture {

    private boolean isBarcodeInput = false;
    private String barcode = "";
    private Consumer<String> barcodeConsumer;
    private Timer timeoutTimer = new Timer(Setting.SCANNER_TIMEOUT.getIntValue(),t ->
    {
        if (isBarcodeInput) {
            String message = barcode.hashCode() == 0
                             ? "Bitte Barcode eingeben:"
                             : "Ein Barcode wurde empfangen, ohne dass die Übertragung beendet wurde. Er kann hier bearbeitet und übernommen werden:";
            String response = barcodeManInput(message, barcode);
            if (response != null) {barcodeConsumer.accept(response);}
            barcode = "";
            isBarcodeInput = false;
            Main.logger.debug("Barcode Scanner timeout");
        }
    });

    private String barcodeManInput(String message, String initValue) {
        return (String) JOptionPane.showInputDialog(null, message, "Barcode Eingabe", JOptionPane.INFORMATION_MESSAGE,
                                                               IconFontSwing.buildIcon(FontAwesome.BARCODE, 64), null, initValue);

    }

    public BarcodeCapture(Consumer<String> barcodeConsumer){
        this.barcodeConsumer = barcodeConsumer;
    };

    public boolean processKeyEvent(KeyEvent e) {
        if (this.isBarcodeInput) {
            timeoutTimer.restart();
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
            isBarcodeInput = true;
            timeoutTimer.start();
            return true;
        } else {
            return false;
        }
    }
}
