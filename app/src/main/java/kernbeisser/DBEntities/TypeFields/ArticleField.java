package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class ArticleField {
public static FieldIdentifier<kernbeisser.DBEntities.Article,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Integer> kbNumber = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Integer.class, "kbNumber");
public static FieldIdentifier<kernbeisser.DBEntities.Article,kernbeisser.DBEntities.PriceList> priceList = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, kernbeisser.DBEntities.PriceList.class, "priceList");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Boolean> weighable = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Boolean.class, "weighable");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Boolean> showInShop = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Boolean.class, "showInShop");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Boolean> active = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Boolean.class, "active");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.time.Instant> activeStateChange = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.time.Instant.class, "activeStateChange");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Boolean> verified = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Boolean.class, "verified");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.lang.String> name = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.lang.String.class, "name");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.lang.String> producer = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.lang.String.class, "producer");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Double> netPrice = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Double.class, "netPrice");
public static FieldIdentifier<kernbeisser.DBEntities.Article,kernbeisser.Enums.MetricUnits> metricUnits = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, kernbeisser.Enums.MetricUnits.class, "metricUnits");
public static FieldIdentifier<kernbeisser.DBEntities.Article,kernbeisser.DBEntities.Supplier> supplier = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, kernbeisser.DBEntities.Supplier.class, "supplier");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Integer> suppliersItemNumber = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Integer.class, "suppliersItemNumber");
public static FieldIdentifier<kernbeisser.DBEntities.Article,kernbeisser.Enums.VAT> vat = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, kernbeisser.Enums.VAT.class, "vat");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Integer> amount = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Integer.class, "amount");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.lang.Long> barcode = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.lang.Long.class, "barcode");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Double> containerSize = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Double.class, "containerSize");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Double> singleDeposit = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Double.class, "singleDeposit");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Double> containerDeposit = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Double.class, "containerDeposit");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.lang.String> info = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.lang.String.class, "info");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.time.Instant> updateDate = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.time.Instant.class, "updateDate");
public static FieldIdentifier<kernbeisser.DBEntities.Article,kernbeisser.DBEntities.SurchargeGroup> surchargeGroup = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, kernbeisser.DBEntities.SurchargeGroup.class, "surchargeGroup");
public static FieldIdentifier<kernbeisser.DBEntities.Article,kernbeisser.Enums.ShopRange> shopRange = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, kernbeisser.Enums.ShopRange.class, "shopRange");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Boolean> offer = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Boolean.class, "offer");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Double> catalogPriceFactor = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Double.class, "catalogPriceFactor");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Integer> labelCount = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Integer.class, "labelCount");
public static FieldIdentifier<kernbeisser.DBEntities.Article,Boolean> labelPerUnit = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, Boolean.class, "labelPerUnit");
public static FieldIdentifier<kernbeisser.DBEntities.Article,java.lang.Double> obsoleteSurcharge = new FieldIdentifier<>(kernbeisser.DBEntities.Article.class, java.lang.Double.class, "obsoleteSurcharge");

}