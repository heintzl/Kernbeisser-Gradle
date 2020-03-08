package kernbeisser.Windows.ItemFilter;

import kernbeisser.Windows.Controller;

public class ItemFilterController implements Controller {
    private ItemFilterView view;
    private ItemFilterModel model;

    ItemFilterController(ItemFilterView view) {
        this.view = view;
        this.model = new ItemFilterModel();
        view.setSuppliers(model.getAllSuppliers());
    }

    @Override
    public void refresh() {

    }

    @Override
    public ItemFilterView getView() {
        return view;
    }

    @Override
    public ItemFilterModel getModel() {
        return model;
    }
}
