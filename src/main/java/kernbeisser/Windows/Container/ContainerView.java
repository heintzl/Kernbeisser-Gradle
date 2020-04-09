package kernbeisser.Windows.Container;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Container;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class ContainerView extends Window {
    private ObjectTable<Container> unpaidContainers;
    private kernbeisser.CustomComponents.PermissionButton commit;
    private ObjectTable<Container> lastContainers;
    private IntegerParseField amount;
    private IntegerParseField kbNumber;
    private IntegerParseField kkNumber;
    private JLabel name;
    private JLabel size;
    private DoubleParseField netPrice;
    private JLabel sellingPrice;
    private JPanel main;
    private JPanel insertSection;
    private JLabel insertSectionLabel;

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

    void setInsertSectionEnabled(boolean b){
        insertSection.setVisible(b);
        insertSectionLabel.setVisible(b);
    }

    Integer getAmount() {
        return amount.getSafeValue();
    }

    void setAmount(String s) {
        amount.setText(s);
    }

    int getKkNumber() {
        return kkNumber.getSafeValue();
    }

    void setKkNumber(String s) {
        kkNumber.setText(s);
    }

    int getNetPrice() {
        return (int) (netPrice.getSafeValue() * 100);
    }

    void setNetPrice(String s) {
        netPrice.setText(s);
    }

    private void createUIComponents() {
        lastContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount, Key.CONTAINER_AMOUNT_READ),
                Column.create("Ladennummer", Container::getKBNumber,Key.CONTAINER_ITEM_READ,Key.ARTICLE_KB_NUMBER_READ),
                Column.create("Kornkraftnummer", e -> e.getItem().getKkNumber(),Key.CONTAINER_ITEM_READ,Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ),
                Column.create("Produktname", e -> e.getItem().getName(),Key.CONTAINER_ITEM_READ,Key.ARTICLE_NAME_READ),
                Column.create("Netto-Preis", e -> e.getNetPrice() + "€",Key.CONTAINER_ITEM_READ,Key.ARTICLE_NET_PRICE_READ),
                Column.create("Verkaufspreis", e -> "notDefined" + "€"),
                new Column<Container>() {
                    @Override
                    public String getName() {
                        return "Kopieren";
                    }

                    @Override
                    public Object getValue(Container container) {
                        return "Kopieren";
                    }

                    @Override
                    public void onAction(Container container) {
                        controller.copy(container);
                    }
                }

        );
        unpaidContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount,Key.CONTAINER_AMOUNT_READ),
                Column.create("Ladennummer", Container::getKBNumber,Key.CONTAINER_ITEM_READ,Key.ARTICLE_KB_NUMBER_READ),
                Column.create("Kornkraftnummer", e -> e.getItem().getKkNumber(),Key.CONTAINER_ITEM_READ,Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ),
                Column.create("Produktname", e -> e.getItem().getName(),Key.CONTAINER_ITEM_READ,Key.ARTICLE_NAME_READ),
                Column.create("Netto-Preis", e -> e.getNetPrice() + "€",Key.CONTAINER_ITEM_READ,Key.ARTICLE_NET_PRICE_READ),
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

    @Override
    public void finish() {
        controller.exit();
    }

    void noItemFound() {
        JOptionPane.showMessageDialog(this,
                                      "Es konnte kein Kornkraft Artikel mit dieser Kornkraft / Kernbeisser Nummer gefunden werden");
    }

}
