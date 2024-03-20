package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;

import java.time.Instant;

public class ShoppingItemField {
public static FieldIdentifier<ShoppingItem,Long> id = new FieldIdentifier<>(ShoppingItem.class, Long.class, "id");
public static FieldIdentifier<ShoppingItem,Integer> amount = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "amount");
public static FieldIdentifier<ShoppingItem,Integer> discount = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "discount");
public static FieldIdentifier<ShoppingItem,Purchase> purchase = new FieldIdentifier<>(ShoppingItem.class, Purchase.class, "purchase");
public static FieldIdentifier<ShoppingItem,String> name = new FieldIdentifier<>(ShoppingItem.class, String.class, "name");
public static FieldIdentifier<ShoppingItem,Integer> kbNumber = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "kbNumber");
public static FieldIdentifier<ShoppingItem,Integer> itemMultiplier = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "itemMultiplier");
public static FieldIdentifier<ShoppingItem, VAT> vat = new FieldIdentifier<>(ShoppingItem.class, VAT.class, "vat");
public static FieldIdentifier<ShoppingItem,Double> vatValue = new FieldIdentifier<>(ShoppingItem.class, Double.class, "vatValue");
public static FieldIdentifier<ShoppingItem, MetricUnits> metricUnits = new FieldIdentifier<>(ShoppingItem.class, MetricUnits.class, "metricUnits");
public static FieldIdentifier<ShoppingItem,Boolean> weighAble = new FieldIdentifier<>(ShoppingItem.class, Boolean.class, "weighAble");
public static FieldIdentifier<ShoppingItem,Integer> suppliersItemNumber = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "suppliersItemNumber");
public static FieldIdentifier<ShoppingItem,String> suppliersShortName = new FieldIdentifier<>(ShoppingItem.class, String.class, "suppliersShortName");
public static FieldIdentifier<ShoppingItem,Double> surcharge = new FieldIdentifier<>(ShoppingItem.class, Double.class, "surcharge");
public static FieldIdentifier<ShoppingItem,Boolean> containerDiscount = new FieldIdentifier<>(ShoppingItem.class, Boolean.class, "containerDiscount");
public static FieldIdentifier<ShoppingItem,Double> itemRetailPrice = new FieldIdentifier<>(ShoppingItem.class, Double.class, "itemRetailPrice");
public static FieldIdentifier<ShoppingItem,Double> itemNetPrice = new FieldIdentifier<>(ShoppingItem.class, Double.class, "itemNetPrice");
public static FieldIdentifier<ShoppingItem,Integer> shoppingCartIndex = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "shoppingCartIndex");
public static FieldIdentifier<ShoppingItem,Long> articleId = new FieldIdentifier<>(ShoppingItem.class, Long.class, "articleId");
public static FieldIdentifier<ShoppingItem,Integer> articleRev = new FieldIdentifier<>(ShoppingItem.class, Integer.class, "articleRev");
public static FieldIdentifier<ShoppingItem,Instant> createDate = new FieldIdentifier<>(ShoppingItem.class, Instant.class, "createDate");
public static FieldIdentifier<ShoppingItem,Double> singleDeposit = new FieldIdentifier<>(ShoppingItem.class, Double.class, "singleDeposit");
public static FieldIdentifier<ShoppingItem,Double> containerDeposit = new FieldIdentifier<>(ShoppingItem.class, Double.class, "containerDeposit");
public static FieldIdentifier<ShoppingItem,Double> containerSize = new FieldIdentifier<>(ShoppingItem.class, Double.class, "containerSize");
public static FieldIdentifier<ShoppingItem,ShoppingItem> parentItem = new FieldIdentifier<>(ShoppingItem.class, ShoppingItem.class, "parentItem");
public static FieldIdentifier<ShoppingItem,Supplier> supplier = new FieldIdentifier<>(ShoppingItem.class, Supplier.class, "supplier");
public static FieldIdentifier<ShoppingItem,Boolean> solidaritySurchargeItem = new FieldIdentifier<>(ShoppingItem.class, Boolean.class, "solidaritySurchargeItem");
public static FieldIdentifier<ShoppingItem,Boolean> depositItem = new FieldIdentifier<>(ShoppingItem.class, Boolean.class, "depositItem");
public static FieldIdentifier<ShoppingItem,Boolean> specialOffer = new FieldIdentifier<>(ShoppingItem.class, Boolean.class, "specialOffer");

}