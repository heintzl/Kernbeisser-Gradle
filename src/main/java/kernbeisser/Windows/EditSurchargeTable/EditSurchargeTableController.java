package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeTableController implements Controller<EditSurchargeTableView,EditSurchargeTableModel> {

    private final EditSurchargeTableModel model;
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
    }

    public void commit() {
        try {
            if (model.doAction(view.getObjectForm().getData())) {
                view.back();
            }
        } catch (CannotParseException e) {
            view.incorrectInput();
            view.getObjectForm().markErrors();
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
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }
}
