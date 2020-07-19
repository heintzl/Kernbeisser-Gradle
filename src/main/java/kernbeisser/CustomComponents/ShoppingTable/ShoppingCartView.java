package kernbeisser.CustomComponents.ShoppingTable;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Collection;

import static java.text.MessageFormat.format;

public class ShoppingCartView extends JPanel implements View<ShoppingCartController> {
    private final ShoppingCartController controller;
    private JLabel sum;
    private JLabel value;
    private JPanel main;
    private ObjectTable<ShoppingItem> shoppingItems;
    private JLabel headerDelete;
    private final boolean editable;

    ShoppingCartView(ShoppingCartController controller, boolean editable) {
        this.controller = controller;
        this.editable = editable;
        headerDelete.setVisible(editable);

    }

    public void setObjects(Collection<ShoppingItem> items) {
        shoppingItems.setObjects(items);
    }

    void clearNodes() {
        shoppingItems.removeAll();
    }

    void setSum(double s) {
        sum.setText(format("{0,number,0.00}€", s));
    }

    void setValue(double s) {
        value.setText(String.format("%.2f€", s));
    }

    public String inputNoOfContainers(ShoppingItem item, boolean retry) {
        String initValue = MessageFormat.format("{0,number,0}",
                                                Math.floor(item.getItemMultiplier() / item.getContainerSize())).trim();
        String message = MessageFormat.format(retry
                                              ? "Eingabe kann nicht verarbeitet werden, bitte noch einmal versuchen. Für wie viele {0,number,0}er Pfand-Gebinde soll Pfand berechnet werden?"
                                              : "Die eingegebene Menge passt in ein oder mehrere {0,number,0}er Pfand-Gebinde. Für wie viele Gebinde soll Pfand berechnet werden?",
                                              item.getContainerSize());
        String response = JOptionPane.showInputDialog(
                getContent(),
                message,
                initValue
        );
        if (response != null) {
            response = response.trim();
        }
        return response;
    }

    private void createUIComponents() {
        int size = 20;
        Font gridFont = new Font("Arial", Font.PLAIN, size);
        EmptyBorder margin = new EmptyBorder(new Insets(10, 10, 10, 10));
        shoppingItems = new ObjectTable<>(
                Column.create("1", e -> {
                    JLabel name = new JLabel(e.getName());
                    name.setBorder(margin);
                    name.setFont(gridFont);
                    return name;
                }),
                Column.create("2", e -> {
                    JLabel discount = new JLabel(e.isContainerDiscount()
                                                 ? "Vorbestellt"
                                                 : (e.getDiscount() != 0
                                                    ? e.getDiscount() + "%"
                                                    : ""));
                    discount.setFont(gridFont);
                    discount.setHorizontalAlignment(SwingConstants.RIGHT);
                    return discount;
                }),
                Column.create("3", e -> {
                    JLabel price = new JLabel(String.format("%.2f€", controller.getPrice(e)));
                    price.setFont(gridFont);
                    price.setHorizontalAlignment(SwingConstants.RIGHT);
                    return price;
                }),
                Column.create("4", e -> {
                    JLabel amount = new JLabel(e.getUnitAmount());
                    amount.setFont(gridFont);
                    amount.setHorizontalAlignment(SwingConstants.RIGHT);
                    return amount;
                }),
                Column.create("5", e -> {
                    JLabel amount = new JLabel(e.getItemMultiplier() + e.getMetricUnits().getShortName());
                    amount.setFont(gridFont);
                    amount.setHorizontalAlignment(SwingConstants.RIGHT);
                    if (!editable) {
                        amount.setBorder(new EmptyBorder(0, 0, 0, 20));
                    }
                    return amount;
                })
        );

        if (editable) {
            shoppingItems.addColumn(
                    Column.create("delete", (e) -> new JPanel() {
                        @Override
                        public void paint(Graphics g) {
                            g.drawImage(IconFontSwing.buildImage(FontAwesome.TRASH, size + 5, Color.RED),
                                        (getWidth() / 2) - (size / 2), 3, null);
                        }
                    }, controller::delete));
        }

        shoppingItems.setRowHeight(size + 10);
        shoppingItems.setGridColor(Color.WHITE);
        shoppingItems.setComplex(true);
        shoppingItems.setTableHeader(null);
    }

    @Override
    public void initialize(ShoppingCartController controller) {
        add(main);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
