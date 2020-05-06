package kernbeisser.CustomComponents.ShoppingTable;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collection;

import static java.text.MessageFormat.format;

public class ShoppingCartView extends JPanel implements View<ShoppingCartController> {
    private final ShoppingCartController controller;
    private JLabel sum;
    private JLabel value;
    private JPanel main;
    private ObjectTable<ShoppingItem> shoppingItems;

    ShoppingCartView(ShoppingCartController controller) {
        this.controller = controller;
    }

    void setObjects(Collection<ShoppingItem> items) {
        shoppingItems.setObjects(items);
    }

    void clearNodes() {
        shoppingItems.removeAll();
    }

    void setSum(double s) {
        sum.setText(format("{0, number, 0.00}€", s));
    }

    void setValue(double s) {
        value.setText(String.format("%.2f€",s));
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
                    JLabel discount = new JLabel(e.getContainerDiscount()
                                                 ? "Vorbestellt"
                                                 : (e.getDiscount() != 0
                                                    ? e.getDiscount() + "%"
                                                    : ""));
                    discount.setFont(gridFont);
                    discount.setHorizontalAlignment(SwingConstants.RIGHT);
                    return discount;
                }),
                Column.create("3", e -> {
                    JLabel price = new JLabel(String.format("%.2f€",controller.getPrice(e)));
                    price.setFont(gridFont);
                    price.setHorizontalAlignment(SwingConstants.RIGHT);
                    return price;
                }),
                Column.create("4", e -> {
                    JLabel amount = new JLabel(e.getItemMultiplier() + e.getMetricUnits().getShortName());
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

    @Override
    public void initialize(ShoppingCartController controller) {
        add(main);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
