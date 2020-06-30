package kernbeisser.Windows.Container;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Container;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class ContainerView implements View<ContainerController> {
    private ObjectTable<Container> unpaidContainers;
    private kernbeisser.CustomComponents.PermissionButton commit;
    private ObjectTable<Container> lastContainers;
    private IntegerParseField amount;
    private IntegerParseField kbNumber;
    private JLabel name;
    private JLabel size;
    private DoubleParseField netPrice;
    private JLabel sellingPrice;
    private JPanel main;
    private JPanel insertSection;
    private JLabel insertSectionLabel;
    private IntegerParseField suppliersItemNumber;

    private final ContainerController controller;

    ContainerView(ContainerController controller) {
        this.controller = controller;
    }

    void setInsertSectionEnabled(boolean b){
        insertSection.setVisible(b);
        insertSectionLabel.setVisible(b);
    }

    int getAmount() {
        return amount.getSafeValue();
    }

    void setAmount(String s) {
        amount.setText(s);
    }

    int getKkNumber() {
        return suppliersItemNumber.getSafeValue();
    }

    void setKkNumber(String s) {
        suppliersItemNumber.setText(s);
    }

    int getNetPrice() {
        return (int) (netPrice.getSafeValue() * 100);
    }

    void setNetPrice(String s) {
        netPrice.setText(s);
    }

    private void createUIComponents() {
        lastContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount, PermissionKey.CONTAINER_AMOUNT_READ),
                Column.create("Ladennummer", Container::getKBNumber, PermissionKey.CONTAINER_ITEM_READ, PermissionKey.ARTICLE_KB_NUMBER_READ),
                Column.create("Kornkraftnummer", e -> e.getItem().getSuppliersItemNumber(), PermissionKey.CONTAINER_ITEM_READ, PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ),
                Column.create("Produktname", e -> e.getItem().getName(), PermissionKey.CONTAINER_ITEM_READ,
                              PermissionKey.ARTICLE_NAME_READ),
                Column.create("Netto-Preis", e -> e.getNetPrice() + "€", PermissionKey.CONTAINER_ITEM_READ,
                              PermissionKey.ARTICLE_NET_PRICE_READ),
                Column.create("Verkaufspreis", e -> "notDefined" + "€")
        );
        unpaidContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount, PermissionKey.CONTAINER_AMOUNT_READ),
                Column.create("Ladennummer", Container::getKBNumber, PermissionKey.CONTAINER_ITEM_READ, PermissionKey.ARTICLE_KB_NUMBER_READ),
                Column.create("Kornkraftnummer", e -> e.getItem().getSuppliersItemNumber(), PermissionKey.CONTAINER_ITEM_READ, PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ),
                Column.create("Produktname", e -> e.getItem().getName(), PermissionKey.CONTAINER_ITEM_READ,
                              PermissionKey.ARTICLE_NAME_READ),
                Column.create("Netto-Preis", e -> e.getNetPrice() + "€", PermissionKey.CONTAINER_ITEM_READ,
                              PermissionKey.ARTICLE_NET_PRICE_READ),
                Column.create("Verkaufspreis", e -> "notDefined" + "€"),
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
        return kbNumber.getSafeValue();
    }

    void setKbNumber(String s) {
        kbNumber.setText(s);
    }


    void noItemFound() {
        JOptionPane.showMessageDialog(getTopComponent(),
                                      "Es konnte kein Kornkraft Artikel mit dieser Kornkraft / Kernbeisser Nummer gefunden werden");
    }

    @Override
    public void initialize(ContainerController controller) {
        commit.addActionListener((e) -> controller.commit());
        suppliersItemNumber.addKeyListener(new KeyAdapter() {
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
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
