package kernbeisser.Windows.EditItem;

import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

public class EditItemController implements Controller {

    private EditItemView view;
    private EditItemModel model;

    public EditItemController(Window current, Article article, Mode mode) {
        model = new EditItemModel(article != null ? article : new Article(), mode);
        if (mode == Mode.REMOVE) {
            model.doAction(article);
            return;
        } else {
            this.view = new EditItemView(this, current);
        }
        view.setPriceLists(model.getAllPriceLists());
        view.setSuppliers(model.getAllSuppliers());
        view.setUnits(model.getAllUnits());
        view.setContainerDefinitions(model.getAllContainerDefinitions());
        view.setVATs(model.getAllVATs());
        view.pasteItem(model.getSource());
    }

    @Override
    public EditItemView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
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
