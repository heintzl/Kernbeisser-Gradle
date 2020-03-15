package kernbeisser.Price;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ShoppingItem;

public class PriceCalculator {
    public static final int CONTAINER_DISCOUNT = Integer.MIN_VALUE;

    public static int getItemPrice(Article article, int discount, int userSurcharge) {
        return getPrice(article.getNetPrice(), article.isVatLow() ? 7 : 19, discount, article.getSurcharge(), userSurcharge);
    }

    public static int getPrice(double netPrice, double vat, double discount, double surcharge, double userSurcharge) {
        return (int) ((netPrice * (1 + (vat / 100f)) * (1 - discount / 100f) * (1 + (surcharge / 100f)) * (1 + (userSurcharge / 100f))) + 0.5);
    }

    public static int getShoppingItemPrice(ShoppingItem item, int userSurcharge) {
        return getPrice((item.isWeighable()
                         ? (item.getItemNetPrice() / 1000f) * item.getAmount() * item.getItemMultiplier()
                         : item.getItemNetPrice() * item.getItemMultiplier()), item.isVatLow() ? 7 : 19,
                        item.getDiscount() == CONTAINER_DISCOUNT ? 1 : item.getDiscount(),
                        item.getDiscount() == CONTAINER_DISCOUNT ? item.getSurcharge() / 2f : item.getSurcharge(),
                        userSurcharge);
    }
}
