package kernbeisser.Windows;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Job;
import kernbeisser.CustomComponents.Node.Node;
import kernbeisser.CustomComponents.Node.NodeList;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class JobSelector extends JFrame {
    private NodeList<Job> selectedJobs;
    private NodeList<Job> jobs;
    public JobSelector(JFrame parent, Set<Job> x){
        EntityManager em = DBConnection.getEntityManager();
        if(parent!=null)
        setComponentOrientation(parent.getComponentOrientation());
        setLocationRelativeTo(parent);
        jobs = new NodeList<>(Node.toNodes(Job.getAll(null)));
        selectedJobs = new NodeList<>(Node.toNodes(x));
        jobs.addNodeSelectionListener(selectedJobs::addNode);
        selectedJobs.addNodeSelectionListener(selectedJobs::removeNode);
        setLayout(new GridLayout(0,2));
        add(jobs);
        add(selectedJobs);
        setVisible(true);
    }
}
