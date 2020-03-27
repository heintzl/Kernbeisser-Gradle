package kernbeisser.Windows.CatalogInput;

import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class CatalogInputView extends Window implements View {
    private JButton importString;
    private JButton importFile;
    private JTextArea stringData;
    private JPanel main;

    CatalogInputView(Window current, CatalogInputController controller) {
        super(current);
        importFile.addActionListener(e -> controller.importFromFile());
        importString.addActionListener(e -> controller.importFromString());
        add(main);
        windowInitialized();
    }

    void extractItemError() {
        JOptionPane.showMessageDialog(this,
                                      "Es liegt ein fehler in der Quelle, ein Artikel kann nicht eingelesen werden!");
    }

    void cannotReadFile() {
        JOptionPane.showMessageDialog(this, "Die angegebene Datei kann nicht gefunden werden!");
    }

    void enableButtons(boolean b) {
        importString.setEnabled(b);
        importFile.setEnabled(b);
    }

    public String getData() {
        return stringData.getText();
    }

    public void success() {
        JOptionPane.showMessageDialog(this, "Der Katalog wurde erfolgreich aktualiesert");
    }

}
