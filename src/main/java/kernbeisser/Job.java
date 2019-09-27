package kernbeisser;

import kernbeisser.Windows.Nodes.JobNode;
import kernbeisser.Windows.Nodes.Node;
import kernbeisser.Windows.Nodes.Nodeable;

import javax.persistence.*;

@Entity
@Table
public class Job implements Nodeable{
    @Id
    @GeneratedValue
    private int jid;

    @Column(unique = true)
    private String name;

    @Column
    private String description;

    public int getId() {
        return jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Node toNode() {
        return new JobNode(this);
    }
}
