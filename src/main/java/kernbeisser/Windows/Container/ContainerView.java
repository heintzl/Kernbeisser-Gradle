package kernbeisser.Windows.Container;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Container;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class ContainerView extends Window{
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

    ContainerView(Window window,ContainerController controller){
        super(window);
        this.controller=controller;
        commit.addActionListener((e) -> controller.commit());
        add(main);
    }

    public double getNetPrice() {
        return netPrice.getValue();
    }

    int getAmount(){
        return amount.getValue();
    }

    int getKkNumber(){
        return kkNumber.getValue();
    }


    private void createUIComponents() {
        lastContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount),
                Column.create("Ladennummer", Container::getKBNumber),
                Column.create("Kornkraftnummer", e -> e.getItem().getKkNumber()),
                Column.create("Produktname", e -> e.getItem().getName()),
                Column.create("Netto-Preis", e -> e.getNetPrice() / 100f + "€"),
                Column.create("Verkaufspreis", e -> e.getPrice() / 100f + "€")
        );
        unpaidContainers = new ObjectTable<>(
                Column.create("Anzahl", Container::getAmount),
                Column.create("Ladennummer", Container::getKBNumber),
                Column.create("Kornkraftnummer", e -> e.getItem().getKkNumber()),
                Column.create("Produktname", e -> e.getItem().getName()),
                Column.create("Netto-Preis", e -> e.getNetPrice() / 100f + "€"),
                Column.create("Verkaufspreis", e -> e.getPrice() / 100f + "€"),
                new Column<Container>(){
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
                        if(container.getId()==0){
                            controller.remove();
                        }
                    }
                }
        );
    }

    Container getSelectedUnpaidOrder(){
        return unpaidContainers.getSelectedObject();
    }

    void setUnpaidContainers(Collection<Container> containers) {
        unpaidContainers.setObjects(containers);
    }
    void setLastContainers(Collection<Container> containers){
        lastContainers.setObjects(containers);
    }

    public int getKbNumber() {
        return kbNumber.getValue();
    }

    @Override
    public void finish() {
        controller.exit();
    }

    void noItemFound() {
        JOptionPane.showMessageDialog(this,"Es konnte kein Kornkraft Artikel mit dieser Kornkraft / Kernbeisser Nummer gefunden werden");
    }
}
