package kernbeisser.Windows.SpecialPriceEditor;

import kernbeisser.CustomComponents.DatePicker.DatePickerController;
import kernbeisser.Main;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class SpecialPriceEditorController {
    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        Main.buildEnvironment();
        new SpecialPriceEditorController(null);
    }
    private final SpecialPriceEditorView view;
    private final SpeicalPriceEditorModel model;
    SpecialPriceEditorController(Window current){
        this.view = new SpecialPriceEditorView(this,current);
        this.model = new SpeicalPriceEditorModel();
        DatePickerController.requestDate(view);
    }
}
