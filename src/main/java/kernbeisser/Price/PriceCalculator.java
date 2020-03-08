package kernbeisser.Price;

import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.VAT;

public class PriceCalculator {
    public static final int CONTAINER_DISCOUNT = Integer.MIN_VALUE;

    public static int getItemPrice(Item item, int discount, int userSurcharge) {
        return getPrice(item.getNetPrice(), item.isVatLow() ? VAT.LOW.getValue() : VAT.HIGH.getValue(), discount, item.getSurcharge(), userSurcharge);
    }

    public static int getPrice(double netPrice, double vat, double discount, double surcharge, double userSurcharge) {
        return (int) ((netPrice * (1 + (vat / 100f)) * (1 - discount / 100f) * (1 + (surcharge / 1000f)) * (1 + (userSurcharge / 100f))) + 0.5);
    }
    public static int getNetFromGross(double grossPrice, boolean vatLow) {
        return (int) (grossPrice / (1 + (vatLow ? VAT.LOW.getValue() : VAT.HIGH.getValue())/ 100f) + 0.5);
    }
    public static int getShoppingItemPrice(ShoppingItem item, int userSurcharge) {
        return getPrice((item.isWeighAble()
                         ? (item.getItemNetPrice() / 1000f) * item.getAmount() * item.getItemAmount()
                         : item.getItemNetPrice() * item.getItemAmount()), item.isVatLow() ? 7 : 19,
                        item.getDiscount() == CONTAINER_DISCOUNT ? 1 : item.getDiscount(),
                        item.getDiscount() == CONTAINER_DISCOUNT ? item.getSurcharge() / 2f : item.getSurcharge(),
                        userSurcharge);
    }
}
