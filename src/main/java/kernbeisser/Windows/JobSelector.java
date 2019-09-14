package kernbeisser.Windows;

import kernbeisser.Tools;
import kernbeisser.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class JobSelector extends JFrame {
    private ArrayList<JCheckBox> jobs = new ArrayList<>();
    JobSelector(List<Boolean> x){
        x.addAll(Tools.createCollection(()->false,20-x.size()));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Wählen sie die gewünschten Dienste aus");
        setResizable(false);
        setSize(500,300);
        setLocationRelativeTo(null);
        setLayout(null);
        for (int i = 0; i < x.size(); i++) {
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.setText(User.getJobName(i));
            jCheckBox.setSelected((x.get(i) !=null&& x.get(i)));
            if(i>9)
                jCheckBox.setBounds(250,20+(i-10)*20,200,20);
            else
            jCheckBox.setBounds(20,20+i*20,200,20);
            jobs.add(jCheckBox);
            add(jCheckBox);
        }
        JButton jButton = new JButton();
        jButton.setText("Fertig");
        jButton.setBounds(20,230,100,30);
        jButton.addActionListener(e -> {
            List<Boolean> out = new ArrayList<>();
            for (JCheckBox job : jobs) {
                out.add(job.isSelected());
            }
            finish(out);
            this.dispose();
        });
        add(jButton);
        setVisible(true);
    }
    abstract void finish(List<Boolean> x);
}
