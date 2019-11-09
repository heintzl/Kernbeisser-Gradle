package kernbeisser.Windows.CatalogInput;

import kernbeisser.Windows.Finishable;

import javax.swing.*;

public abstract class CatalogInput extends JFrame implements Finishable {
    private CatalogInputController controller = new CatalogInputController();
    private JButton refreshCatalog;
    private JProgressBar progress;
    private JButton chooseFile;
    private JTextArea data;
    private JPanel main;

    public CatalogInput() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        add(main);
        chooseFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
                /*try {
                    controller.importData(fileChooser.getSelectedFile());
                } catch (ObjectParseException ex) {
                    JOptionPane.showMessageDialog(this,ex.getMessage());
                } catch (FileReadException ex) {
                    JOptionPane.showMessageDialog(this,ex.getMessage());
                }*/
            }
        });
        refreshCatalog.addActionListener((e)->{
            /*try {
                //controller.importData(data.getText());
            } catch (ObjectParseException ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }*/
        });
    }

}
