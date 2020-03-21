package kernbeisser.Windows.Container;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Container;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class ContainerView extends Window {
    private ObjectTable<Container> unpaidContainers;
    private JButton commit;
    private ObjectTable<Container> lastContainers;
    private IntegerParseField amount;
    private IntegerParseField kbNumber;
    private IntegerParseField kkNumber;
    private JLabel name;
    private JLabel size;
    private DoubleParseField netPrice;
    private JLabel sellingPrice;
    private JPanel main;

    private ContainerController controller;

    ContainerView(Window window, ContainerController controller) {
        super(window);
        this.controller = controller;
        commit.addActionListener((e) -> controller.commit());
        kkNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.searchKK();
            }
        });
        kbNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.searchKB();
            }
        });
        add(main);
        windowInitialized();
    }

    int getAmount() {
        return amount.getValue();
    }

    void setAmount(String s) {
        amount.setText(s);
    }

    int getKkNumber() {
        return kkNumber.getValue();
    }

    void setKkNumber(String s) {
        kkNumber.setText(s);
    }

    int getNetPrice() {
        return (int) (netPrice.getValue() * 100);
    }

    void setNetPrice(String s) {
        netPrice.setText(s);
    }

    private void createUIComponents() {
        lastContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount),
                Column.create("Ladennummer", Container::getKBNumber),
                Column.create("Kornkraftnummer", e -> e.getItem().getKkNumber()),
                Column.create("Produktname", e -> e.getItem().getName()),
                Column.create("Netto-Preis", e -> e.getNetPrice() + "€"),
                Column.create("Verkaufspreis", e -> e.getPrice() + "€")
        );
        unpaidContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount),
                Column.create("Ladennummer", Container::getKBNumber),
                Column.create("Kornkraftnummer", e -> e.getItem().getKkNumber()),
                Column.create("Produktname", e -> e.getItem().getName()),
                Column.create("Netto-Preis", e -> e.getNetPrice() + "€"),
                Column.create("Verkaufspreis", e -> e.getPrice() + "€"),
                new Column<Container>() {
                    @Override
                    public String getName() {
                        return "Löschen";
                    }

                    @Override
                    public Object getValue(Container container) {
                        return container.getId() == 0 ? "Löschen" : "";
                    }

                    @Override
                    public void onAction(Container container) {
                        if (container.getId() == 0) {
                            controller.remove();
                        }
                    }
                }
        );
    }

    void setItemName(String s) {
        name.setText(s);
    }

    void setItemSize(String s) {
        size.setText(s);
    }

    void setSellingPrice(String s) {
        sellingPrice.setText(s);
    }

    Container getSelectedUnpaidOrder() {
        return unpaidContainers.getSelectedObject();
    }

    void setUnpaidContainers(Collection<Container> containers) {
        unpaidContainers.setObjects(containers);
    }

    void setLastContainers(Collection<Container> containers) {
        lastContainers.setObjects(containers);
    }

    public int getKbNumber() {
        return kbNumber.getValue();
    }

    void setKbNumber(String s) {
        kbNumber.setText(s);
    }

    @Override
    public void finish() {
        controller.exit();
    }

    void noItemFound() {
        JOptionPane.showMessageDialog(this,
                                      "Es konnte kein Kornkraft Artikel mit dieser Kornkraft / Kernbeisser Nummer gefunden werden");
    }

}
