package kernbeisser.Windows.JobSelector;

import kernbeisser.DBEntitys.Job;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class JobSelectorModel implements Model {
    private Collection<Job> currentJobs;
    JobSelectorModel(Collection<Job> currentJobs){
        this.currentJobs=currentJobs;
    }

    public Collection<Job> getCurrentJobs() {
        return currentJobs;
    }

    public void setCurrentJobs(Collection<Job> newValue) {
        currentJobs.clear();
        currentJobs.addAll(newValue);
    }

    Collection<Job> getAllJobs(){
        return Job.getAll(null);
    }
}
