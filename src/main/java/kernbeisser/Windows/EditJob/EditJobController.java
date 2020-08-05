package kernbeisser.Windows.EditJob;

import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Proxy;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class EditJobController implements Controller<EditJobView,EditJobModel> {

    private final EditJobModel model;
    private final EditJobView view;

    public EditJobController(Job job, Mode mode) {
        model = new EditJobModel(job != null ? job : Proxy.getSecureInstance(new Job()),mode);
        view = new EditJobView();
    }


    @NotNull
    @Override
    public EditJobView getView() {
        return view;
    }

    @NotNull
    @Override
    public EditJobModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public void commit() {
        view.getForm().applyMode(model.getMode());
    }
}
