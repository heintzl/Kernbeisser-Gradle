package kernbeisser.Windows.CatalogInput;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CatalogInputView implements View<CatalogInputController> {
    private JButton importString;
    private JButton importFile;
    private JTextArea stringData;
    private JPanel main;

    private CatalogInputController controller;

    CatalogInputView(CatalogInputController controller) {
        this.controller = controller;
    }

    void extractItemError() {
        JOptionPane.showMessageDialog(getTopComponent(),
                                      "Es liegt ein fehler in der Quelle, ein Artikel kann nicht eingelesen werden!");
    }

    void cannotReadFile() {
        JOptionPane.showMessageDialog(getTopComponent(), "Die angegebene Datei kann nicht gefunden werden!");
    }

    void enableButtons(boolean b) {
        importString.setEnabled(b);
        importFile.setEnabled(b);
    }

    public String getData() {
        return stringData.getText();
    }

    public void success() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der Katalog wurde erfolgreich aktualiesert");
    }

    @Override
    public void initialize(CatalogInputController controller) {
        importFile.addActionListener(e -> controller.importFromFile());
        importString.addActionListener(e -> controller.importFromString());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
