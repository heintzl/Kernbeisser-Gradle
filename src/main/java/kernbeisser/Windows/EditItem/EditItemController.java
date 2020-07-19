package kernbeisser.Windows.EditItem;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EditItemController implements Controller<EditItemView,EditItemModel> {

    private EditItemView view;
    private final EditItemModel model;

    public EditItemController(Article article, Mode mode) {
        model = new EditItemModel(article != null ? article : new Article(), mode);
        if (mode == Mode.REMOVE) {
            model.doAction(article);
            return;
        } else {
            this.view = new EditItemView();
        }
        switch (mode) {
            case ADD:
                view.setActionTitle("Als neuen Artikel aufnehmen");
                view.setActionIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, new Color(0x00EE00)));
                break;
            case EDIT:
                view.setActionTitle("Änderungen übernehmen");
                view.setActionIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(0x0000BB)));
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
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    void doAction() {
        if (!view.validate()) {
            view.invalidInput();
            return;
        }
        Article data;
        try {
            data = view.getArticleObjectForm().getData();
        } catch (CannotParseException e) {
            view.invalidInput();
            return;
        }
        if (model.getMode() == Mode.ADD) {
            if (EditItemModel.nameExists(data.getName())) {
                view.nameAlreadyExists();
                return;
            }
            if (model.kbNumberExists(data.getKbNumber()) > -1) {
                if (view.kbNumberAlreadyExists()) {
                    view.setKbNumber(model.nextUnusedArticleNumber(data.getKbNumber()));
                }
                return;
            }
            if (data.getBarcode() != null) {
                if (model.barcodeExists(data.getBarcode()) > -1) {
                    view.barcodeAlreadyExists();
                    return;
                }
            }
        }
        if (model.getMode() == Mode.EDIT) {
            if ((!data.getName().equals(model.getSource().getName())) && EditItemModel.nameExists(data.getName())) {
                view.nameAlreadyExists();
                return;
            }
            int idOfKBNumber = model.kbNumberExists(data.getKbNumber());
            if (idOfKBNumber != -1 && idOfKBNumber != model.getSource().getId()) {
                if (view.kbNumberAlreadyExists()) {
                    view.setKbNumber(model.nextUnusedArticleNumber(data.getKbNumber()));
                }
                return;
            }
            if (data.getBarcode() != null) {
                int idOfBarcode = model.barcodeExists(data.getBarcode());
                if (idOfBarcode != -1 && idOfBarcode != model.getSource().getId()) {
                    view.barcodeAlreadyExists();
                    return;
                }
            }
        }
        if (model.doAction(data.unwrapProxy())) {
            view.back();
        }
    }
}
