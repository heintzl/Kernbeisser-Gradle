package kernbeisser;

import kernbeisser.Windows.Nodes.JobNode;
import kernbeisser.CustomComponents.Node.Node;
import kernbeisser.CustomComponents.Node.Nodeable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

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

    @CreationTimestamp
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

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
