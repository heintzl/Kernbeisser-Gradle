package kernbeisser.Price;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;

public class PriceCalculator {
    public static final int CONTAINER_DISCOUNT = Integer.MIN_VALUE;

    public static double getItemPrice(Article article, double discount, double userSurcharge) {
        return getPrice(article.getNetPrice(), article.getVAT().getValue(), discount, article.getSurcharge(), userSurcharge);
    }

    public static double getPrice(double netPrice, double vat, double discount, double surcharge, double userSurcharge) {
        return Math.round(100f * (netPrice * (1 + vat) * (1 - discount) * (1 + surcharge) * (1 + (userSurcharge)))) / 100f;
    }

public static double getNetFromGross(double grossPrice, double vat) {
        return (double) grossPrice / (1 + vat);
    }

        public static double getShoppingItemPrice(ShoppingItem item, double userSurcharge) {
        return getPrice((item.isWeighable()
                         ? (item.getItemNetPrice() / 1000) * item.getAmount() * item.getItemMultiplier()
                         : item.getItemNetPrice() * item.getItemMultiplier()), item.getVat(),
                        item.getDiscount() == CONTAINER_DISCOUNT ? 1 : item.getDiscount(),
                        item.getDiscount() == CONTAINER_DISCOUNT ? item.getSurcharge() * Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue() : item.getSurcharge(),
                        userSurcharge);
    }
}
