package kernbeisser.Windows.Purchase;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class PurchaseView extends Window implements View {
    private JButton finish;
    private ObjectTable<ShoppingItem> items;
    private JLabel date;
    private JLabel sum;
    private JLabel count;
    private JLabel seller;
    private JLabel customer;
    private JPanel main;


    PurchaseView(Window current,PurchaseController controller){
        super(current);
        add(main);
        finish.addActionListener((e) -> back());
        setSize(current.getSize());
        setLocationRelativeTo(current);
    }

    void setDate(String date){
        this.date.setText(date);
    }

    void setCustomer(String customerName){
        customer.setText(customerName);
    }

    void setSeller(String sellerName){
        seller.setText(sellerName);
    }

    void setSum(long sum){
        this.sum.setText(sum / 100f + "€");
    }
    void setItemCount(int c){
        count.setText(c+"");
    }

    void setItems(Collection<ShoppingItem> items){
        this.items.setObjects(items);
    }

    private void createUIComponents() {
        items = new ObjectTable<ShoppingItem>(
                Column.create("Artikelname",ShoppingItem::getName),
                Column.create("Anzahl",ShoppingItem::getItemAmount),
                Column.create("Verkaufs Preis",e -> e.getRawPrice()/100f+"€"),
                Column.create("Netto Preis",ShoppingItem::getNetPrice)
        );
    }
}
