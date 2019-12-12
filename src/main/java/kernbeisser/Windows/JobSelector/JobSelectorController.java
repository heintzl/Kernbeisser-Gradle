package kernbeisser.Windows.JobSelector;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

public class JobSelectorController implements Controller {
    private JobSelectorModel model;
    private JobSelectorView view;

    JobSelectorController(JobSelectorView view){
        this.view=view;
        this.model=new JobSelectorModel();
    }

    @Override
    public void refresh() {

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
