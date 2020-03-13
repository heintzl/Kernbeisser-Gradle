package kernbeisser.Windows.JobSelector;

import kernbeisser.DBEntities.Job;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

import java.util.Collection;

public class JobSelectorController implements Controller {
    private JobSelectorModel model;
    private JobSelectorView view;

    public JobSelectorController(Window current, Collection<Job> jobs) {
        this.view = new JobSelectorView(current, this);
        this.model = new JobSelectorModel(jobs);

        view.fillSelectedJobs(model.getCurrentJobs());
        Collection<Job> availableJobs = model.getAllJobs();
        model.getCurrentJobs().forEach(availableJobs::remove);
        view.fillAvailableJobs(availableJobs);
    }

    @Override
    public void refresh() {

    }

    void overrideCurrentJobs() {
        Collection<Job> newContent = view.getSelectedJobs();
        Collection<Job> currentContent = model.getCurrentJobs();
        currentContent.clear();
        currentContent.addAll(newContent);
    }

    @Override
    public JobSelectorView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
