package kernbeisser.Windows;

import kernbeisser.DBConnection;
import kernbeisser.Job;
import kernbeisser.Tools;
import kernbeisser.User;
import kernbeisser.Windows.Nodes.Node;
import kernbeisser.Windows.Nodes.NodeList;
import kernbeisser.Windows.Nodes.Nodeable;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JobSelector extends JFrame {
    private NodeList<Job> selectedJobs;
    private NodeList<Job> jobs;
    JobSelector(JFrame parent,Set<Job> x){
        EntityManager em = DBConnection.getEntityManager();
        if(parent!=null)
        setComponentOrientation(parent.getComponentOrientation());
        setLocationRelativeTo(parent);
        jobs = new NodeList<>(Node.toNodes(em.createQuery("select j from Job j", Job.class).getResultList()));
        selectedJobs = new NodeList<>(Node.toNodes(x));
        jobs.addNodeSelectionListener(selectedJobs::addNode);
        selectedJobs.addNodeSelectionListener(selectedJobs::removeNode);
        setLayout(new GridLayout(0,2));
        add(jobs);
        add(selectedJobs);
        setVisible(true);
    }
}
