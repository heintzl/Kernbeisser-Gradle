package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;

import java.time.Instant;

public class ArticleField {
public static FieldIdentifier<Article,Integer> id = new FieldIdentifier<>(Article.class, Integer.class, "id");
public static FieldIdentifier<Article,Integer> kbNumber = new FieldIdentifier<>(Article.class, Integer.class, "kbNumber");
public static FieldIdentifier<Article,PriceList> priceList = new FieldIdentifier<>(Article.class, PriceList.class, "priceList");
public static FieldIdentifier<Article,Boolean> weighable = new FieldIdentifier<>(Article.class, Boolean.class, "weighable");
public static FieldIdentifier<Article,Boolean> showInShop = new FieldIdentifier<>(Article.class, Boolean.class, "showInShop");
public static FieldIdentifier<Article,Boolean> active = new FieldIdentifier<>(Article.class, Boolean.class, "active");
public static FieldIdentifier<Article,Instant> activeStateChange = new FieldIdentifier<>(Article.class, Instant.class, "activeStateChange");
public static FieldIdentifier<Article,Boolean> verified = new FieldIdentifier<>(Article.class, Boolean.class, "verified");
public static FieldIdentifier<Article,String> name = new FieldIdentifier<>(Article.class, String.class, "name");
public static FieldIdentifier<Article,String> producer = new FieldIdentifier<>(Article.class, String.class, "producer");
public static FieldIdentifier<Article,Double> netPrice = new FieldIdentifier<>(Article.class, Double.class, "netPrice");
public static FieldIdentifier<Article, MetricUnits> metricUnits = new FieldIdentifier<>(Article.class, MetricUnits.class, "metricUnits");
public static FieldIdentifier<Article,Supplier> supplier = new FieldIdentifier<>(Article.class, Supplier.class, "supplier");
public static FieldIdentifier<Article,Integer> suppliersItemNumber = new FieldIdentifier<>(Article.class, Integer.class, "suppliersItemNumber");
public static FieldIdentifier<Article, VAT> vat = new FieldIdentifier<>(Article.class, VAT.class, "vat");
public static FieldIdentifier<Article,Integer> amount = new FieldIdentifier<>(Article.class, Integer.class, "amount");
public static FieldIdentifier<Article,Long> barcode = new FieldIdentifier<>(Article.class, Long.class, "barcode");
public static FieldIdentifier<Article,Double> containerSize = new FieldIdentifier<>(Article.class, Double.class, "containerSize");
public static FieldIdentifier<Article,Double> singleDeposit = new FieldIdentifier<>(Article.class, Double.class, "singleDeposit");
public static FieldIdentifier<Article,Double> containerDeposit = new FieldIdentifier<>(Article.class, Double.class, "containerDeposit");
public static FieldIdentifier<Article,String> info = new FieldIdentifier<>(Article.class, String.class, "info");
public static FieldIdentifier<Article,Instant> updateDate = new FieldIdentifier<>(Article.class, Instant.class, "updateDate");
public static FieldIdentifier<Article,SurchargeGroup> surchargeGroup = new FieldIdentifier<>(Article.class, SurchargeGroup.class, "surchargeGroup");
public static FieldIdentifier<Article, ShopRange> shopRange = new FieldIdentifier<>(Article.class, ShopRange.class, "shopRange");
public static FieldIdentifier<Article,Boolean> offer = new FieldIdentifier<>(Article.class, Boolean.class, "offer");
public static FieldIdentifier<Article,Double> catalogPriceFactor = new FieldIdentifier<>(Article.class, Double.class, "catalogPriceFactor");
public static FieldIdentifier<Article,Integer> labelCount = new FieldIdentifier<>(Article.class, Integer.class, "labelCount");
public static FieldIdentifier<Article,Boolean> labelPerUnit = new FieldIdentifier<>(Article.class, Boolean.class, "labelPerUnit");
public static FieldIdentifier<Article,Double> obsoleteSurcharge = new FieldIdentifier<>(Article.class, Double.class, "obsoleteSurcharge");

}