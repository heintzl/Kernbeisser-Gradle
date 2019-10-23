/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.Nodes;

import kernbeisser.CustomComponents.Node.Node;
import kernbeisser.Job;

import javax.swing.*;

/**
 *
 * @author julik
 */
public class JobNode extends javax.swing.JPanel implements Node<Job> {

    private Job job;

    /**
     * Creates new form JobNode
     */
    public JobNode(Job job) {
        initComponents();
        jobName.setText(job.getName());
        description.setText(job.getDescription());
        this.job=job;
    }

    @Override
    public Job get() {
        return job;
    }

    @Override
    public JPanel getGraphic() {
        return this;
    }

    @Override
    public void set(Job job) {
        this.job=job;
    }

    @Override
    public int hashCode() {
        return job.getId()+job.getName().hashCode();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jobName = new javax.swing.JLabel();
        description = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setPreferredSize(new java.awt.Dimension(359, 100));

        jobName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jobName.setText("JobName");

        description.setText("Description");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jobName)
                    .addComponent(description))
                .addContainerGap(366, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jobName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(description)
                .addContainerGap(75, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel description;
    private javax.swing.JLabel jobName;
    // End of variables declaration//GEN-END:variables
}
