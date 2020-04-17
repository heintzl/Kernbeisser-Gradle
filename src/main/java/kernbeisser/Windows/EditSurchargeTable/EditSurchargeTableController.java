package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeTableController implements Controller<EditSurchargeTableView,EditSurchargeTableModel> {

    private EditSurchargeTableModel model;
    private EditSurchargeTableView view;

    public EditSurchargeTableController(SurchargeTable surchargeTable, Mode mode) {
        this.model = new EditSurchargeTableModel(surchargeTable == null ? new SurchargeTable() : surchargeTable, mode);
        if (mode == Mode.REMOVE) {
            model.doAction(surchargeTable);
            return;
        } else {
            this.view = new EditSurchargeTableView(this);
        }
        view.setSuppliers(model.getAllSuppliers());
        view.paste(model.getSurchargeTable());
    }

    public void commit() {
        if (model.doAction(view.collect(model.getSurchargeTable()))) {
            view.back();
        }
    }

    @Override
    public @NotNull EditSurchargeTableView getView() {
        return view;
    }

    @Override
    public @NotNull EditSurchargeTableModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
