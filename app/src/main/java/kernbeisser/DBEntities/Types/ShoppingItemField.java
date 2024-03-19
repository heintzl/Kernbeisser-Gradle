package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;

public class ShoppingItemField {
  public static FieldIdentifier<ShoppingItem, Long> id =
      new FieldIdentifier<>(ShoppingItem.class, "id");
  public static FieldIdentifier<ShoppingItem, Integer> amount =
      new FieldIdentifier<>(ShoppingItem.class, "amount");
  public static FieldIdentifier<ShoppingItem, Integer> discount =
      new FieldIdentifier<>(ShoppingItem.class, "discount");
  public static FieldIdentifier<ShoppingItem, Purchase> purchase =
      new FieldIdentifier<>(ShoppingItem.class, "purchase");
  public static FieldIdentifier<ShoppingItem, String> name =
      new FieldIdentifier<>(ShoppingItem.class, "name");
  public static FieldIdentifier<ShoppingItem, Integer> kbNumber =
      new FieldIdentifier<>(ShoppingItem.class, "kbNumber");
  public static FieldIdentifier<ShoppingItem, Integer> itemMultiplier =
      new FieldIdentifier<>(ShoppingItem.class, "itemMultiplier");
  public static FieldIdentifier<ShoppingItem, VAT> vat =
      new FieldIdentifier<>(ShoppingItem.class, "vat");
  public static FieldIdentifier<ShoppingItem, Double> vatValue =
      new FieldIdentifier<>(ShoppingItem.class, "vatValue");
  public static FieldIdentifier<ShoppingItem, MetricUnits> metricUnits =
      new FieldIdentifier<>(ShoppingItem.class, "metricUnits");
  public static FieldIdentifier<ShoppingItem, Boolean> weighAble =
      new FieldIdentifier<>(ShoppingItem.class, "weighAble");
  public static FieldIdentifier<ShoppingItem, Integer> suppliersItemNumber =
      new FieldIdentifier<>(ShoppingItem.class, "suppliersItemNumber");
  public static FieldIdentifier<ShoppingItem, String> suppliersShortName =
      new FieldIdentifier<>(ShoppingItem.class, "suppliersShortName");
  public static FieldIdentifier<ShoppingItem, Double> surcharge =
      new FieldIdentifier<>(ShoppingItem.class, "surcharge");
  public static FieldIdentifier<ShoppingItem, Boolean> containerDiscount =
      new FieldIdentifier<>(ShoppingItem.class, "containerDiscount");
  public static FieldIdentifier<ShoppingItem, Double> itemRetailPrice =
      new FieldIdentifier<>(ShoppingItem.class, "itemRetailPrice");
  public static FieldIdentifier<ShoppingItem, Double> itemNetPrice =
      new FieldIdentifier<>(ShoppingItem.class, "itemNetPrice");
  public static FieldIdentifier<ShoppingItem, Integer> shoppingCartIndex =
      new FieldIdentifier<>(ShoppingItem.class, "shoppingCartIndex");
  public static FieldIdentifier<ShoppingItem, Long> articleId =
      new FieldIdentifier<>(ShoppingItem.class, "articleId");
  public static FieldIdentifier<ShoppingItem, Integer> articleRev =
      new FieldIdentifier<>(ShoppingItem.class, "articleRev");
  public static FieldIdentifier<ShoppingItem, Instant> createDate =
      new FieldIdentifier<>(ShoppingItem.class, "createDate");
  public static FieldIdentifier<ShoppingItem, Double> singleDeposit =
      new FieldIdentifier<>(ShoppingItem.class, "singleDeposit");
  public static FieldIdentifier<ShoppingItem, Double> containerDeposit =
      new FieldIdentifier<>(ShoppingItem.class, "containerDeposit");
  public static FieldIdentifier<ShoppingItem, Double> containerSize =
      new FieldIdentifier<>(ShoppingItem.class, "containerSize");
  public static FieldIdentifier<ShoppingItem, ShoppingItem> parentItem =
      new FieldIdentifier<>(ShoppingItem.class, "parentItem");
  public static FieldIdentifier<ShoppingItem, Supplier> supplier =
      new FieldIdentifier<>(ShoppingItem.class, "supplier");
  public static FieldIdentifier<ShoppingItem, Boolean> solidaritySurchargeItem =
      new FieldIdentifier<>(ShoppingItem.class, "solidaritySurchargeItem");
  public static FieldIdentifier<ShoppingItem, Boolean> depositItem =
      new FieldIdentifier<>(ShoppingItem.class, "depositItem");
  public static FieldIdentifier<ShoppingItem, Boolean> specialOffer =
      new FieldIdentifier<>(ShoppingItem.class, "specialOffer");
}
