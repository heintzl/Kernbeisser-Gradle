package kernbeisser.DBEntities.Types;

import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;

import java.time.Instant;

public class ArticleField {
	public static final FieldIdentifier<Article, Integer> id = new FieldIdentifier<>(Article.class, "id");
	public static final FieldIdentifier<Article, Integer> kbNumber =
			new FieldIdentifier<>(Article.class, "kbNumber");
	public static final FieldIdentifier<Article, PriceList> priceList =
			new FieldIdentifier<>(Article.class, "priceList");
	public static final FieldIdentifier<Article, Boolean> weighable =
			new FieldIdentifier<>(Article.class, "weighable");
	public static final FieldIdentifier<Article, Boolean> showInShop =
			new FieldIdentifier<>(Article.class, "showInShop");
	public static final FieldIdentifier<Article, Boolean> active =
			new FieldIdentifier<>(Article.class, "active");
	public static final FieldIdentifier<Article, Instant> activeStateChange =
			new FieldIdentifier<>(Article.class, "activeStateChange");
	public static final FieldIdentifier<Article, Boolean> verified =
			new FieldIdentifier<>(Article.class, "verified");
	public static final FieldIdentifier<Article, String> name =
			new FieldIdentifier<>(Article.class, "name");
	public static final FieldIdentifier<Article, String> producer =
			new FieldIdentifier<>(Article.class, "producer");
	public static final FieldIdentifier<Article, Double> netPrice =
			new FieldIdentifier<>(Article.class, "netPrice");
	public static final FieldIdentifier<Article, MetricUnits> metricUnits =
			new FieldIdentifier<>(Article.class, "metricUnits");
	public static final FieldIdentifier<Article, Supplier> supplier =
			new FieldIdentifier<>(Article.class, "supplier");
	public static final FieldIdentifier<Article, Integer> suppliersItemNumber =
			new FieldIdentifier<>(Article.class, "suppliersItemNumber");
	public static final FieldIdentifier<Article, VAT> vat = new FieldIdentifier<>(Article.class, "vat");
	public static final FieldIdentifier<Article, Integer> amount =
			new FieldIdentifier<>(Article.class, "amount");
	public static final FieldIdentifier<Article, Long> barcode =
			new FieldIdentifier<>(Article.class, "barcode");
	public static final FieldIdentifier<Article,Double> containerSize =
			new FieldIdentifier<>(Article.class, "containerSize");
	public static final FieldIdentifier<Article, Double> singleDeposit =
			new FieldIdentifier<>(Article.class, "singleDeposit");
	public static final FieldIdentifier<Article, Double> containerDeposit =
			new FieldIdentifier<>(Article.class, "containerDeposit");
	public static final FieldIdentifier<Article, String> info =
			new FieldIdentifier<>(Article.class, "info");
	public static final FieldIdentifier<Article, Instant> updateDate =
			new FieldIdentifier<>(Article.class, "updateDate");
	public static final FieldIdentifier<Article, SurchargeGroup> surchargeGroup =
			new FieldIdentifier<>(Article.class, "surchargeGroup");
	public static final FieldIdentifier<Article, ShopRange> shopRange =
			new FieldIdentifier<>(Article.class, "shopRange");
	public static final FieldIdentifier<Article, Offer> offer =
			new FieldIdentifier<>(Article.class, "offer");
	public static final FieldIdentifier<Article,Double> catalogPriceFactor =
			new FieldIdentifier<>(Article.class, "catalogPriceFactor");
	public static final FieldIdentifier<Article, Integer> labelCount =
			new FieldIdentifier<>(Article.class, "labelCount");
	public static final FieldIdentifier<Article, Boolean> labelPerUnit =
			new FieldIdentifier<>(Article.class, "labelPerUnit");
	public static final FieldIdentifier<Article, Double> obsoleteSurcharge =
			new FieldIdentifier<>(Article.class, "obsoleteSurcharge");
}
