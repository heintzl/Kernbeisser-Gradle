package kernbeisser.Windows.SpecialPriceEditor;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class SpecialPriceEditorView extends Window implements View {
    public SpecialPriceEditorView(SpecialPriceEditorController controller,Window currentWindow, Key... required) {
        super(currentWindow, required);
    }
}
