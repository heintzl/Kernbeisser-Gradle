package kernbeisser.Windows.JobSelector;

import kernbeisser.DBEntitys.Job;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class JobSelectorModel implements Model {
    Collection<Job> getAllJobs(){
        return Job.getAll(null);
    }
}
