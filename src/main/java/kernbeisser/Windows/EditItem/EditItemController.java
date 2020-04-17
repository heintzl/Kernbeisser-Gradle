package kernbeisser.Windows.EditItem;

import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

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
