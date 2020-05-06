package kernbeisser.Windows.EditItem;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EditItemController implements Controller<EditItemView,EditItemModel> {

    private EditItemView view;
    private EditItemModel model;

    public EditItemController(Article article, Mode mode) {
        model = new EditItemModel(article != null ? article : new Article(), mode);
        if (mode == Mode.REMOVE) {
            model.doAction(article);
            return;
        } else {
            this.view = new EditItemView(this);
        }
        switch (mode){
            case ADD:
                view.setActionTitle("Als neuen Artikel aufnehmen");
                view.setActionIcon(IconFontSwing.buildIcon(FontAwesome.PLUS,20,new Color(0x00EE00)));
                break;
            case EDIT:
                view.setActionTitle("Änderungen übernehmen");
                view.setActionIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL,20,new Color(0x0000BB)));
                break;
        }
    }

    @Override
    public @NotNull EditItemView getView() {
        return view;
    }

    @Override
    public @NotNull EditItemModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setPriceLists(model.getAllPriceLists());
        view.setSuppliers(model.getAllSuppliers());
        view.setUnits(model.getAllUnits());
        view.setContainerDefinitions(model.getAllContainerDefinitions());
        view.setVATs(model.getAllVATs());
        view.pasteItem(model.getSource());
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    void doAction() {
        Article data = view.collectItem(model.getSource());
        if (model.getMode() == Mode.ADD) {
            if (model.kbNumberExists(data.getKbNumber())) {
                view.kbNumberAlreadyExists();
                return;
            }
            if (data.getBarcode() != null) {
                if (model.barcodeExists(data.getBarcode())) {
                    view.barcodeAlreadyExists();
                }
            }
        }
        if (model.doAction(view.collectItem(model.getSource()))) {
            view.back();
        }
    }
}
