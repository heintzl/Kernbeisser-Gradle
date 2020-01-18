package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Window;

public class EditSurchargeTableController {

    private EditSurchargeTableModel model;
    private EditSurchargeTableView view;

    public EditSurchargeTableController(Window current, SurchargeTable surchargeTable, Mode mode){
        this.model = new EditSurchargeTableModel(surchargeTable == null ? new SurchargeTable() : surchargeTable,mode);
        this.view = new EditSurchargeTableView(this,current);
        view.setSuppliers(model.getAllSuppliers());
        view.paste(model.getSurchargeTable());
    }

    public void commit() {
        model.doAction(view.collect(model.getSurchargeTable()));
    }
}
