package kernbeisser.Windows.JobSelector;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Job;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class JobSelectorView extends Window implements View {
    private JPanel mainPanel;
    private JButton move;
    private JButton finish;
    private JButton cancel;
    private ObjectTable<Job> availableJobs;
    private ObjectTable<Job> selectedJobs;


    public JobSelectorView(Window current, JobSelectorController controller) {
        super(current);
        add(mainPanel);
        setSize(500, 500);
        setLocationRelativeTo(current);
        cancel.addActionListener(e -> back());
        move.addActionListener(e -> {
            Job job = availableJobs.getSelectedObject();
            if (job != null) {
                selectedJobs.add(job);
                availableJobs.remove(job);
            }
            job = selectedJobs.getSelectedObject();
            if (job != null) {
                availableJobs.add(job);
                selectedJobs.remove(job);
            }
        });
        finish.addActionListener(e -> {
            controller.overrideCurrentJobs();
            back();
        });
        availableJobs.addSelectionListener(job -> {
            selectedJobs.add(job);
            availableJobs.remove(job);
        });
        selectedJobs.addSelectionListener(job -> {
            availableJobs.add(job);
            selectedJobs.remove(job);
        });
        windowInitialized();
    }

    private void createUIComponents() {
        availableJobs = new ObjectTable<>(
                Column.create("Name", Job::getName),
                Column.create("Description", Job::getDescription)
        );
        selectedJobs = new ObjectTable<>(
                Column.create("Name", Job::getName),
                Column.create("Description", Job::getDescription)
        );
    }

    Collection<Job> getSelectedJobs() {
        return selectedJobs.getItems();
    }

    void fillAvailableJobs(Collection<Job> jobs) {
        availableJobs.setObjects(jobs);
    }

    void fillSelectedJobs(Collection<Job> jobs) {
        selectedJobs.setObjects(jobs);
    }

}
