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


    PurchaseView(Window current,PurchaseController controller){
        super(current);
        finish.addActionListener((e) -> back());
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

    void setSum(int sum){
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
                Column.create("Verkaufs Preis",ShoppingItem::getRawPrice),
                Column.create("Netto Preis",ShoppingItem::getNetPrice)
        );
    }
}
