package kernbeisser.Windows.JobSelector;

import kernbeisser.DBEntitys.Job;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

import java.util.Collection;
import java.util.List;

public class JobSelectorController implements Controller {
    private JobSelectorModel model;
    private JobSelectorView view;

    JobSelectorController(JobSelectorView view, Collection<Job> jobs){
        this.view=view;
        this.model=new JobSelectorModel(jobs);

        view.fillSelectedJobs(model.getCurrentJobs());
        Collection<Job> availableJobs = model.getAllJobs();
        model.getCurrentJobs().forEach(availableJobs::remove);
        view.fillAvailableJobs(availableJobs);
    }

    @Override
    public void refresh() {

    }

    void overrideCurrentJobs(){
        Collection<Job> newContent = view.getSelectedJobs();
        Collection<Job> currentContent = model.getCurrentJobs();
        currentContent.clear();
        currentContent.addAll(newContent);
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
