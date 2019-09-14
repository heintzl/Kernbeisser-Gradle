package kernbeisser;

import javax.swing.*;

public class DropHandler extends TransferHandler {
    @Override
    public boolean importData(TransferSupport support) {
        System.out.println(support.getComponent().toString());
        return false;
    }
}
