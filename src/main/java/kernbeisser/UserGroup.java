package kernbeisser;

import javax.persistence.*;

@Table
@Entity
class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int gid;

    @Column
    private int value;
}