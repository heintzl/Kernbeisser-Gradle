package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class ShoppingItemField {
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Long> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Long.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> amount =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "amount");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> discount =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "discount");
  public static FieldIdentifier<
          kernbeisser.DBEntities.ShoppingItem, kernbeisser.DBEntities.Purchase>
      purchase =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ShoppingItem.class,
              kernbeisser.DBEntities.Purchase.class,
              "purchase");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, java.lang.String> name =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, java.lang.String.class, "name");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> kbNumber =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "kbNumber");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> itemMultiplier =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "itemMultiplier");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, kernbeisser.Enums.VAT> vat =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, kernbeisser.Enums.VAT.class, "vat");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> vatValue =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Double.class, "vatValue");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, kernbeisser.Enums.MetricUnits>
      metricUnits =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ShoppingItem.class,
              kernbeisser.Enums.MetricUnits.class,
              "metricUnits");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Boolean> weighAble =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Boolean.class, "weighAble");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> suppliersItemNumber =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "suppliersItemNumber");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, java.lang.String>
      suppliersShortName =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ShoppingItem.class,
              java.lang.String.class,
              "suppliersShortName");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> surcharge =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Double.class, "surcharge");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Boolean> containerDiscount =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Boolean.class, "containerDiscount");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> itemRetailPrice =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Double.class, "itemRetailPrice");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> itemNetPrice =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Double.class, "itemNetPrice");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> shoppingCartIndex =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "shoppingCartIndex");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Long> articleId =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Long.class, "articleId");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Integer> articleRev =
      new FieldIdentifier<>(kernbeisser.DBEntities.ShoppingItem.class, Integer.class, "articleRev");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, java.time.Instant> createDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, java.time.Instant.class, "createDate");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> singleDeposit =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Double.class, "singleDeposit");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> containerDeposit =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Double.class, "containerDeposit");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Double> containerSize =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Double.class, "containerSize");
  public static FieldIdentifier<
          kernbeisser.DBEntities.ShoppingItem, kernbeisser.DBEntities.ShoppingItem>
      parentItem =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ShoppingItem.class,
              kernbeisser.DBEntities.ShoppingItem.class,
              "parentItem");
  public static FieldIdentifier<
          kernbeisser.DBEntities.ShoppingItem, kernbeisser.DBEntities.Supplier>
      supplier =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ShoppingItem.class,
              kernbeisser.DBEntities.Supplier.class,
              "supplier");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Boolean>
      solidaritySurchargeItem =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ShoppingItem.class, Boolean.class, "solidaritySurchargeItem");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Boolean> depositItem =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Boolean.class, "depositItem");
  public static FieldIdentifier<kernbeisser.DBEntities.ShoppingItem, Boolean> specialOffer =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.ShoppingItem.class, Boolean.class, "specialOffer");
}
