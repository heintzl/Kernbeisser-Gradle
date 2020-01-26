package kernbeisser.Windows.CatalogInput;

import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class CatalogInputView extends Window implements View {
    private JButton importString;
    private JButton importFile;
    private JTextArea stringData;

    CatalogInputView(Window current, CatalogInputController controller){
        super(current);
        importFile.addActionListener(e -> controller.importFromFile());
        importString.addActionListener(e -> controller.importFromString());
    }

    void extractItemError() {
        JOptionPane.showMessageDialog(this,"Es liegt ein fehler in der Quelle, ein Artikel kann nicht eingelesen werden!");
    }

    void cannotReadFile() {
        JOptionPane.showMessageDialog(this,"Die angegebene Datei kann nicht gefunden werden!");
    }

    public String getData() {
        return stringData.getText();
    }
}
