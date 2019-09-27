package kernbeisser;

import javax.persistence.*;

@Entity
@Table
public class ShoppingItem {
    @Id
    private int siid;
    @ManyToOne
    @JoinColumn
    private Item item;
    @Column
    private int amount;
    @Column
    private int price;
    @JoinColumn
    @ManyToOne
    private ShoppingSession shoppingSession;
}
