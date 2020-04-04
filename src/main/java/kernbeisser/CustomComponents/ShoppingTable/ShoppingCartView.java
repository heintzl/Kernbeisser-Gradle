package kernbeisser.CustomComponents.ShoppingTable;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collection;

public class ShoppingCartView extends JPanel implements View {
    private final ShoppingCartController controller;
    private JLabel sum;
    private JLabel value;
    private JPanel main;
    private ObjectTable<ShoppingItem> shoppingItems;

    ShoppingCartView(ShoppingCartController controller) {
        this.controller = controller;
        add(main);
    }

    void setObjects(Collection<ShoppingItem> items) {
        shoppingItems.setObjects(items);
    }

    void clearNodes() {
        shoppingItems.removeAll();
    }

    void setSum(int s) {
        sum.setText(s  + "€");
    }

    void setValue(double s) {
        value.setText(s  + "€");
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
                    JLabel discount = new JLabel(e.getDiscount() == PriceCalculator.CONTAINER_DISCOUNT
                                                 ? "Vorbestellt"
                                                 : e.getDiscount() + "%");
                    discount.setFont(gridFont);
                    discount.setHorizontalAlignment(SwingConstants.RIGHT);
                    return discount;
                }),
                Column.create("3", e -> {
                    JLabel price = new JLabel(controller.getPrice(e)  + "€");
                    price.setFont(gridFont);
                    price.setHorizontalAlignment(SwingConstants.RIGHT);
                    return price;
                }),
                Column.create("4", e -> {
                    JLabel amount = new JLabel(
                            e.isWeighable()
                            ? (e.getMetricUnits().toUnit(e.getAmount() * e.getItemMultiplier()) + e.getMetricUnits()
                                                                                                   .getShortName())
                            : e.getItemMultiplier() + MetricUnits.STACK.getShortName());
                    amount.setFont(gridFont);
                    amount.setHorizontalAlignment(SwingConstants.RIGHT);
                    return amount;
                }),
                Column.create("delete", (e) -> new JPanel() {
                    @Override
                    public void paint(Graphics g) {
                        g.drawImage(IconFontSwing.buildImage(FontAwesome.TRASH, size + 5, Color.RED),
                                    (getWidth() / 2) - (size / 2), 3, null);
                    }
                }, controller::delete)
        );
        shoppingItems.setRowHeight(size + 10);
        shoppingItems.setGridColor(Color.WHITE);
        shoppingItems.setComplex(true);
        shoppingItems.setTableHeader(null);
    }
}
