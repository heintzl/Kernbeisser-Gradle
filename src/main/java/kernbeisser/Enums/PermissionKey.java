package kernbeisser.Enums;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import kernbeisser.DBEntities.*;
import kernbeisser.Security.ActionPermission;
import kernbeisser.Security.PermissionSet;

public enum PermissionKey {

  // (only for CollectionProxy)
  READ_ITERABLE_VALUE(null),
  MODIFY_ITERABLE_VALUE(null),
  CHANGE_ALL(null),

  // VALUE
  GO_UNDER_MIN(ActionPermission.class),

  // Window Permissions
  ACTION_OPEN_PERMISSION_ASSIGNMENT(ActionPermission.class),
  ACTION_OPEN_ADMIN_TOOLS(ActionPermission.class),
  ACTION_OPEN_MANAGE_PRICELISTS(ActionPermission.class),
  ACTION_OPEN_SOLO_SHOPPING_MASK(ActionPermission.class),
  ACTION_OPEN_APPLICATION_SETTINGS(ActionPermission.class),
  ACTION_OPEN_PERMISSION_MANAGEMENT(ActionPermission.class),
  ACTION_OPEN_SPECIAL_PRICE_EDITOR(ActionPermission.class),
  ACTION_OPEN_TRANSACTION(ActionPermission.class),
  ACTION_OPEN_ACCOUNTING_REPORTS(ActionPermission.class),
  ACTION_OPEN_CASHIER_SHOPPING_MASK(ActionPermission.class),
  ACTION_OPEN_CHANGE_PASSWORD(ActionPermission.class),
  ACTION_OPEN_EDIT_ARTICLES(ActionPermission.class),
  ACTION_OPEN_EDIT_JOBS(ActionPermission.class),
  ACTION_OPEN_EDIT_SUPPLIERS(ActionPermission.class),
  ACTION_OPEN_EDIT_SURCHARGE_GROUPS(ActionPermission.class),
  ACTION_OPEN_EDIT_USER_GROUP(ActionPermission.class),
  ACTION_OPEN_EDIT_USER_SETTING(ActionPermission.class),
  ACTION_OPEN_EDIT_USERS(ActionPermission.class),
  ACTION_OPEN_MANAGE_PRICE_LISTS(ActionPermission.class),
  ACTION_OPEN_PRE_ORDER(ActionPermission.class),
  ACTION_OPEN_OWN_PRE_ORDER(ActionPermission.class),
  ACTION_OPEN_SUPPLY(ActionPermission.class),
  ACTION_OPEN_SYNCHRONISE_ARTICLE_WINDOW(ActionPermission.class),
  ACTION_OPEN_DB_LOG_IN(ActionPermission.class),

  // Actions
  ACTION_ADD_BEGINNER(ActionPermission.class),
  ACTION_EDIT_OWN_DATA(ActionPermission.class),
  ACTION_LOGIN(ActionPermission.class),
  ACTION_TRANSACTION_FROM_OTHER(ActionPermission.class),
  ACTION_TRANSACTION_FROM_KB(ActionPermission.class),
  ACTION_ORDER_CONTAINER(ActionPermission.class),
  ACTION_ORDER_OWN_CONTAINER(ActionPermission.class),
  ACTION_GRANT_CASHIER_PERMISSION(ActionPermission.class),

  ADD_ARTICLE(Article.class),
  REMOVE_ARTICLE(Article.class),
  EDIT_ARTICLE(Article.class),
  ARTICLE_KB_NUMBER_READ(Article.class),
  ARTICLE_KB_NUMBER_WRITE(Article.class),
  ARTICLE_SURCHARGE_READ(Article.class),
  ARTICLE_SURCHARGE_WRITE(Article.class),
  ARTICLE_PRICE_LIST_READ(Article.class),
  ARTICLE_PRICE_LIST_WRITE(Article.class),
  ARTICLE_CONTAINER_DEF_READ(Article.class),
  ARTICLE_CONTAINER_DEF_WRITE(Article.class),
  ARTICLE_WEIGH_ABLE_READ(Article.class),
  ARTICLE_WEIGH_ABLE_WRITE(Article.class),
  ARTICLE_LISTED_READ(Article.class),
  ARTICLE_LISTED_WRITE(Article.class),
  ARTICLE_SHOW_IN_SHOP_READ(Article.class),
  ARTICLE_SHOW_IN_SHOP_WRITE(Article.class),
  ARTICLE_ACTIVE_READ(Article.class),
  ARTICLE_ACTIVE_WRITE(Article.class),
  ARTICLE_PRINT_AGAIN_READ(Article.class),
  ARTICLE_PRINT_AGAIN_WRITE(Article.class),
  ARTICLE_DELETE_ALLOWED_READ(Article.class),
  ARTICLE_DELETE_ALLOWED_WRITE(Article.class),
  ARTICLE_LOSS_READ(Article.class),
  ARTICLE_LOSS_WRITE(Article.class),
  ARTICLE_INFO_READ(Article.class),
  ARTICLE_INFO_WRITE(Article.class),
  ARTICLE_SHOP_RANGE_READ(Article.class),
  ARTICLE_SHOP_RANGE_WRITE(Article.class),
  ARTICLE_SOLD_READ(Article.class),
  ARTICLE_SOLD_WRITE(Article.class),
  ARTICLE_DELIVERED_READ(Article.class),
  ARTICLE_DELIVERED_WRITE(Article.class),
  ARTICLE_INTAKE_READ(Article.class),
  ARTICLE_INTAKE_WRITE(Article.class),
  ARTICLE_LAST_DELIVERY_READ(Article.class),
  ARTICLE_LAST_DELIVERY_WRITE(Article.class),
  ARTICLE_DELETED_DATE_READ(Article.class),
  ARTICLE_DELETED_DATE_WRITE(Article.class),
  ARTICLE_COOLING_READ(Article.class),
  ARTICLE_COOLING_WRITE(Article.class),
  ARTICLE_VERIFIED_READ(Article.class),
  ARTICLE_VERIFIED_WRITE(Article.class),
  ARTICLE_ID_READ(Article.class),
  ARTICLE_ID_WRITE(Article.class),
  ARTICLE_NAME_READ(Article.class),
  ARTICLE_NAME_WRITE(Article.class),
  ARTICLE_PRODUCER_READ(Article.class),
  ARTICLE_PRODUCER_WRITE(Article.class),
  ARTICLE_NET_PRICE_READ(Article.class),
  ARTICLE_NET_PRICE_WRITE(Article.class),
  ARTICLE_METRIC_UNITS_READ(Article.class),
  ARTICLE_METRIC_UNITS_WRITE(Article.class),
  ARTICLE_SUPPLIER_READ(Article.class),
  ARTICLE_SUPPLIER_WRITE(Article.class),
  ARTICLE_SUPPLIERS_ITEM_NUMBER_READ(Article.class),
  ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE(Article.class),
  ARTICLE_VAT_READ(Article.class),
  ARTICLE_VAT_WRITE(Article.class),
  ARTICLE_AMOUNT_READ(Article.class),
  ARTICLE_AMOUNT_WRITE(Article.class),
  ARTICLE_BARCODE_READ(Article.class),
  ARTICLE_BARCODE_WRITE(Article.class),
  ARTICLE_CONTAINER_SIZE_READ(Article.class),
  ARTICLE_CONTAINER_SIZE_WRITE(Article.class),
  ARTICLE_SINGLE_DEPOSIT_READ(Article.class),
  ARTICLE_SINGLE_DEPOSIT_WRITE(Article.class),
  ARTICLE_CONTAINER_DEPOSIT_READ(Article.class),
  ARTICLE_CONTAINER_DEPOSIT_WRITE(Article.class),
  ARTICLE_OFFER_READ(Article.class),
  ARTICLE_OFFER_WRITE(Article.class),
  CONTAINER_ID_READ(PreOrder.class),
  CONTAINER_ID_WRITE(PreOrder.class),
  CONTAINER_ITEM_READ(PreOrder.class),
  CONTAINER_ITEM_WRITE(PreOrder.class),
  CONTAINER_USER_READ(PreOrder.class),
  CONTAINER_USER_WRITE(PreOrder.class),
  CONTAINER_USER_SURCHARGE_READ(PreOrder.class),
  CONTAINER_USER_SURCHARGE_WRITE(PreOrder.class),
  CONTAINER_INFO_READ(PreOrder.class),
  CONTAINER_INFO_WRITE(PreOrder.class),
  CONTAINER_AMOUNT_READ(PreOrder.class),
  CONTAINER_AMOUNT_WRITE(PreOrder.class),
  CONTAINER_NET_PRICE_READ(PreOrder.class),
  CONTAINER_NET_PRICE_WRITE(PreOrder.class),
  CONTAINER_PAYED_READ(PreOrder.class),
  CONTAINER_PAYED_WRITE(PreOrder.class),
  CONTAINER_DELIVERY_READ(PreOrder.class),
  CONTAINER_DELIVERY_WRITE(PreOrder.class),
  CONTAINER_CREATE_DATE_READ(PreOrder.class),
  CONTAINER_CREATE_DATE_WRITE(PreOrder.class),
  INVENTORY_ID_READ(Inventory.class),
  INVENTORY_ID_WRITE(Inventory.class),
  INVENTORY_CREATION_TIME_STAMP_READ(Inventory.class),
  INVENTORY_CREATION_TIME_STAMP_WRITE(Inventory.class),
  INVENTORY_STATE_ID_READ(InventoryState.class),
  INVENTORY_STATE_ID_WRITE(InventoryState.class),
  INVENTORY_STATE_ARTICLE_READ(InventoryState.class),
  INVENTORY_STATE_ARTICLE_WRITE(InventoryState.class),
  INVENTORY_STATE_COUNT_READ(InventoryState.class),
  INVENTORY_STATE_COUNT_WRITE(InventoryState.class),
  ADD_JOB(Job.class),
  EDIT_JOB(Job.class),
  REMOVE_JOB(Job.class),
  JOB_ID_READ(Job.class),
  JOB_ID_WRITE(Job.class),
  JOB_NAME_READ(Job.class),
  JOB_NAME_WRITE(Job.class),
  JOB_DESCRIPTION_READ(Job.class),
  JOB_DESCRIPTION_WRITE(Job.class),
  JOB_CREATE_DATE_READ(Job.class),
  JOB_CREATE_DATE_WRITE(Job.class),
  JOB_UPDATE_DATE_READ(Job.class),
  JOB_UPDATE_DATE_WRITE(Job.class),
  OFFER_ID_READ(Offer.class),
  OFFER_ID_WRITE(Offer.class),
  OFFER_SPECIAL_NET_PRICE_READ(Offer.class),
  OFFER_SPECIAL_NET_PRICE_WRITE(Offer.class),
  OFFER_FROM_DATE_READ(Offer.class),
  OFFER_FROM_DATE_WRITE(Offer.class),
  OFFER_TO_DATE_READ(Offer.class),
  OFFER_TO_DATE_WRITE(Offer.class),
  OFFER_REPEAT_MODE_READ(Offer.class),
  OFFER_REPEAT_MODE_WRITE(Offer.class),
  OFFER_OFFER_ARTICLE_READ(Offer.class),
  OFFER_OFFER_ARTICLE_WRITE(Offer.class),
  OFFER_PARENT_ARTICLE_READ(Offer.class),
  OFFER_PARENT_ARTICLE_WRITE(Offer.class),
  PERMISSION_ID_READ(Permission.class),
  PERMISSION_ID_WRITE(Permission.class),
  PERMISSION_NAME_READ(Permission.class),
  PERMISSION_NAME_WRITE(Permission.class),
  PERMISSION_KEY_SET_READ(Permission.class),
  PERMISSION_KEY_SET_WRITE(Permission.class),
  PRICE_LIST_ID_READ(PriceList.class),
  PRICE_LIST_ID_WRITE(PriceList.class),
  PRICE_LIST_NAME_READ(PriceList.class),
  PRICE_LIST_NAME_WRITE(PriceList.class),
  PRICE_LIST_SUPER_PRICE_LIST_READ(PriceList.class),
  PRICE_LIST_SUPER_PRICE_LIST_WRITE(PriceList.class),
  PRICE_LIST_UPDATE_DATE_READ(PriceList.class),
  PRICE_LIST_UPDATE_DATE_WRITE(PriceList.class),
  PRICE_LIST_CREATE_DATE_READ(PriceList.class),
  PRICE_LIST_CREATE_DATE_WRITE(PriceList.class),
  PURCHASE_ID_READ(Purchase.class),
  PURCHASE_ID_WRITE(Purchase.class),
  PURCHASE_SESSION_READ(Purchase.class),
  PURCHASE_SESSION_WRITE(Purchase.class),
  PURCHASE_CREATE_DATE_READ(Purchase.class),
  PURCHASE_CREATE_DATE_WRITE(Purchase.class),
  PURCHASE_USER_SURCHARGE_READ(Purchase.class),
  PURCHASE_USER_SURCHARGE_WRITE(Purchase.class),
  SALE_SESSION_ID_READ(SaleSession.class),
  SALE_SESSION_ID_WRITE(SaleSession.class),
  SALE_SESSION_CUSTOMER_READ(SaleSession.class),
  SALE_SESSION_CUSTOMER_WRITE(SaleSession.class),
  SALE_SESSION_SECOND_SELLER_READ(SaleSession.class),
  SALE_SESSION_SECOND_SELLER_WRITE(SaleSession.class),
  SALE_SESSION_SELLER_READ(SaleSession.class),
  SALE_SESSION_SELLER_WRITE(SaleSession.class),
  SETTING_VALUE_ID_READ(SettingValue.class),
  SETTING_VALUE_ID_WRITE(SettingValue.class),
  SETTING_VALUE_SETTING_READ(SettingValue.class),
  SETTING_VALUE_SETTING_WRITE(SettingValue.class),
  SETTING_VALUE_VALUE_READ(SettingValue.class),
  SETTING_VALUE_VALUE_WRITE(SettingValue.class),
  SHELF_ID_READ(Shelf.class),
  SHELF_ID_WRITE(Shelf.class),
  SHELF_LOCATION_READ(Shelf.class),
  SHELF_LOCATION_WRITE(Shelf.class),
  SHELF_ARTICLES_READ(Shelf.class),
  SHELF_ARTICLES_WRITE(Shelf.class),
  SHOPPING_ITEM_ID_READ(ShoppingItem.class),
  SHOPPING_ITEM_ID_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_AMOUNT_READ(ShoppingItem.class),
  SHOPPING_ITEM_AMOUNT_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_DISCOUNT_READ(ShoppingItem.class),
  SHOPPING_ITEM_DISCOUNT_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_PURCHASE_READ(ShoppingItem.class),
  SHOPPING_ITEM_PURCHASE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_NAME_READ(ShoppingItem.class),
  SHOPPING_ITEM_NAME_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_KB_NUMBER_READ(ShoppingItem.class),
  SHOPPING_ITEM_KB_NUMBER_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_ITEM_MULTIPLIER_READ(ShoppingItem.class),
  SHOPPING_ITEM_ITEM_MULTIPLIER_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_VAT_READ(ShoppingItem.class),
  SHOPPING_ITEM_VAT_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_VATVALUE_READ(ShoppingItem.class),
  SHOPPING_ITEM_VATVALUE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_METRIC_UNITS_READ(ShoppingItem.class),
  SHOPPING_ITEM_METRIC_UNITS_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_WEIGH_ABLE_READ(ShoppingItem.class),
  SHOPPING_ITEM_WEIGH_ABLE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_READ(ShoppingItem.class),
  SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_SHORT_NAME_READ(ShoppingItem.class),
  SHOPPING_ITEM_SHORT_NAME_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_SURCHARGE_READ(ShoppingItem.class),
  SHOPPING_ITEM_SURCHARGE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_CONTAINER_DISCOUNT_READ(ShoppingItem.class),
  SHOPPING_ITEM_CONTAINER_DISCOUNT_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ(ShoppingItem.class),
  SHOPPING_ITEM_ITEM_RETAIL_PRICE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_ITEM_NET_PRICE_READ(ShoppingItem.class),
  SHOPPING_ITEM_ITEM_NET_PRICE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_SHOPPING_CART_INDEX_READ(ShoppingItem.class),
  SHOPPING_ITEM_SHOPPING_CART_INDEX_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_SINGLE_DEPOSIT_READ(ShoppingItem.class),
  SHOPPING_ITEM_SINGLE_DEPOSIT_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_CONTAINER_DEPOSIT_READ(ShoppingItem.class),
  SHOPPING_ITEM_CONTAINER_DEPOSIT_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_CONTAINER_SIZE_READ(ShoppingItem.class),
  SHOPPING_ITEM_CONTAINER_SIZE_WRITE(ShoppingItem.class),
  SHOPPING_ITEM_SUPER_INDEX_READ(ShoppingItem.class),
  SHOPPING_ITEM_SUPER_INDEX_WRITE(ShoppingItem.class),
  ADD_SUPPLIER(Supplier.class),
  REMOVE_SUPPLIER(Supplier.class),
  EDIT_SUPPLIER(Supplier.class),
  SUPPLIER_ID_READ(Supplier.class),
  SUPPLIER_ID_WRITE(Supplier.class),
  SUPPLIER_NAME_READ(Supplier.class),
  SUPPLIER_NAME_WRITE(Supplier.class),
  SUPPLIER_PHONE_NUMBER_READ(Supplier.class),
  SUPPLIER_PHONE_NUMBER_WRITE(Supplier.class),
  SUPPLIER_FAX_READ(Supplier.class),
  SUPPLIER_FAX_WRITE(Supplier.class),
  SUPPLIER_STREET_READ(Supplier.class),
  SUPPLIER_STREET_WRITE(Supplier.class),
  SUPPLIER_LOCATION_READ(Supplier.class),
  SUPPLIER_LOCATION_WRITE(Supplier.class),
  SUPPLIER_EMAIL_READ(Supplier.class),
  SUPPLIER_EMAIL_WRITE(Supplier.class),
  SUPPLIER_SHORT_NAME_READ(Supplier.class),
  SUPPLIER_SHORT_NAME_WRITE(Supplier.class),
  SUPPLIER_SURCHARGE_READ(Supplier.class),
  SUPPLIER_SURCHARGE_WRITE(Supplier.class),
  SUPPLIER_KEEPER_READ(Supplier.class),
  SUPPLIER_KEEPER_WRITE(Supplier.class),
  SUPPLIER_CREATE_DATE_READ(Supplier.class),
  SUPPLIER_CREATE_DATE_WRITE(Supplier.class),
  SUPPLIER_UPDATE_DATE_READ(Supplier.class),
  SUPPLIER_UPDATE_DATE_WRITE(Supplier.class),
  SURCHARGE_TABLE_ID_READ(SurchargeGroup.class),
  SURCHARGE_TABLE_ID_WRITE(SurchargeGroup.class),
  SURCHARGE_TABLE_SURCHARGE_READ(SurchargeGroup.class),
  SURCHARGE_TABLE_SURCHARGE_WRITE(SurchargeGroup.class),
  SURCHARGE_TABLE_FROM_READ(SurchargeGroup.class),
  SURCHARGE_TABLE_FROM_WRITE(SurchargeGroup.class),
  SURCHARGE_TABLE_TO_READ(SurchargeGroup.class),
  SURCHARGE_TABLE_TO_WRITE(SurchargeGroup.class),
  SURCHARGE_TABLE_NAME_READ(SurchargeGroup.class),
  SURCHARGE_TABLE_NAME_WRITE(SurchargeGroup.class),
  SURCHARGE_TABLE_SUPPLIER_READ(SurchargeGroup.class),
  SURCHARGE_TABLE_SUPPLIER_WRITE(SurchargeGroup.class),
  TRANSACTION_ID_READ(Transaction.class),
  TRANSACTION_ID_WRITE(Transaction.class),
  TRANSACTION_VALUE_READ(Transaction.class),
  TRANSACTION_VALUE_WRITE(Transaction.class),
  TRANSACTION_TRANSACTION_TYPE_READ(Transaction.class),
  TRANSACTION_TRANSACTION_TYPE_WRITE(Transaction.class),
  TRANSACTION_FROM_READ(Transaction.class),
  TRANSACTION_FROM_WRITE(Transaction.class),
  TRANSACTION_FROMUG_READ(Transaction.class),
  TRANSACTION_FROMUG_WRITE(Transaction.class),
  TRANSACTION_TO_READ(Transaction.class),
  TRANSACTION_TO_WRITE(Transaction.class),
  TRANSACTION_TOUG_READ(Transaction.class),
  TRANSACTION_TOUG_WRITE(Transaction.class),
  TRANSACTION_DATE_READ(Transaction.class),
  TRANSACTION_DATE_WRITE(Transaction.class),
  TRANSACTION_INFO_READ(Transaction.class),
  TRANSACTION_INFO_WRITE(Transaction.class),
  TRANSACTION_CREATEDBY_READ(Transaction.class),
  TRANSACTION_CREATEDBY_WRITE(Transaction.class),
  ADD_USER(User.class),
  EDIT_USER(User.class),
  REMOVE_USER(User.class),
  USER_ID_READ(User.class),
  USER_ID_WRITE(User.class),
  USER_PERMISSIONS_READ(User.class),
  USER_PERMISSIONS_WRITE(User.class),
  USER_SHARES_READ(User.class),
  USER_SHARES_WRITE(User.class),
  USER_JOBS_READ(User.class),
  USER_JOBS_WRITE(User.class),
  USER_KERNBEISSER_KEY_READ(User.class),
  USER_KERNBEISSER_KEY_WRITE(User.class),
  USER_EMPLOYEE_READ(User.class),
  USER_EMPLOYEE_WRITE(User.class),
  USER_USERNAME_READ(User.class),
  USER_USERNAME_WRITE(User.class),
  USER_PASSWORD_READ(User.class),
  USER_PASSWORD_WRITE(User.class),
  USER_FIRST_NAME_READ(User.class),
  USER_FIRST_NAME_WRITE(User.class),
  USER_SURNAME_READ(User.class),
  USER_SURNAME_WRITE(User.class),
  USER_PHONE_NUMBER1_READ(User.class),
  USER_PHONE_NUMBER1_WRITE(User.class),
  USER_PHONE_NUMBER2_READ(User.class),
  USER_PHONE_NUMBER2_WRITE(User.class),
  USER_STREET_READ(User.class),
  USER_STREET_WRITE(User.class),
  USER_TOWN_READ(User.class),
  USER_TOWN_WRITE(User.class),
  USER_TOWN_CODE_READ(User.class),
  USER_TOWN_CODE_WRITE(User.class),
  USER_EMAIL_READ(User.class),
  USER_EMAIL_WRITE(User.class),
  USER_CREATE_DATE_READ(User.class),
  USER_CREATE_DATE_WRITE(User.class),
  USER_UPDATE_DATE_READ(User.class),
  USER_UPDATE_DATE_WRITE(User.class),
  USER_UPDATE_BY_READ(User.class),
  USER_UPDATE_BY_WRITE(User.class),
  USER_USER_GROUP_READ(User.class),
  USER_USER_GROUP_WRITE(User.class),
  USER_UNREADABLE_READ(User.class),
  USER_UNREADABLE_WRITE(User.class),
  USER_LAST_PASSWORD_CHANGE_READ(User.class),
  USER_LAST_PASSWORD_CHANGE_WRITE(User.class),
  USER_FORCE_PASSWORD_CHANGE_READ(User.class),
  USER_FORCE_PASSWORD_CHANGE_WRITE(User.class),
  USER_GROUP_ID_READ(UserGroup.class),
  USER_GROUP_ID_WRITE(UserGroup.class),
  USER_GROUP_UPDATE_DATE_READ(UserGroup.class),
  USER_GROUP_UPDATE_DATE_WRITE(UserGroup.class),
  USER_GROUP_UPDATE_BY_READ(UserGroup.class),
  USER_GROUP_UPDATE_BY_WRITE(UserGroup.class),
  USER_GROUP_VALUE_READ(UserGroup.class),
  USER_GROUP_VALUE_WRITE(UserGroup.class),
  USER_GROUP_INTEREST_THIS_YEAR_READ(UserGroup.class),
  USER_GROUP_INTEREST_THIS_YEAR_WRITE(UserGroup.class),
  USER_GROUP_SOLIDARITY_SURCHARGE_READ(UserGroup.class),
  USER_GROUP_SOLIDARITY_SURCHARGE_WRITE(UserGroup.class),
  USER_SETTING_VALUE_ID_READ(UserSettingValue.class),
  USER_SETTING_VALUE_ID_WRITE(UserSettingValue.class),
  USER_SETTING_VALUE_USER_READ(UserSettingValue.class),
  USER_SETTING_VALUE_USER_WRITE(UserSettingValue.class),
  USER_SETTING_VALUE_USER_SETTING_READ(UserSettingValue.class),
  USER_SETTING_VALUE_USER_SETTING_WRITE(UserSettingValue.class),
  USER_SETTING_VALUE_VALUE_READ(UserSettingValue.class),
  USER_SETTING_VALUE_VALUE_WRITE(UserSettingValue.class);

  private final Class<?> clazz;

  PermissionKey(Class<?> clazz) {
    this.clazz = clazz;
  }

  public PermissionKey getWriteKey() {
    return valueOf(name().replace("_READ", "_WRITE"));
  }

  public PermissionKey getReadKey() {
    return valueOf(name().replace("_WRITE", "_READ"));
  }

  public static Collection<PermissionKey> find(Class<?> category) {
    Collection<PermissionKey> out = new ArrayList<>();
    for (PermissionKey value : values()) {
      if (value.clazz == category) {
        out.add(value);
      }
    }
    return out;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public static Collection<PermissionKey> find(Class<?> category, boolean read, boolean write) {
    Collection<PermissionKey> out = new ArrayList<>();
    for (PermissionKey value : values()) {
      if (value.clazz == category) {
        if ((read && value.name().endsWith("READ")) || (write && value.name().endsWith("WRITE"))) {
          out.add(value);
        }
      }
    }
    return out;
  }

  public static PermissionKey[] combine(PermissionKey[]... permissionKeys) {
    return Arrays.stream(permissionKeys).flatMap(Arrays::stream).toArray(PermissionKey[]::new);
  }

  public static PermissionSet getNonAdminPermissions() {
    return PermissionSet.asPermissionSet(
        new PermissionKey[] {
          ACTION_EDIT_OWN_DATA,
          ACTION_OPEN_EDIT_USER_GROUP,
          ACTION_OPEN_OWN_PRE_ORDER,
          ACTION_OPEN_SOLO_SHOPPING_MASK,
          GO_UNDER_MIN,
          ACTION_ORDER_OWN_CONTAINER
        });
  }

  public static PermissionKey[] allReadPermissions(Class<?> c) {
    return with(e -> e.clazz != null && e.clazz.equals(c) && e.name().endsWith("READ"));
  }

  public static PermissionKey[] allWritePermissions(Class<?> c) {
    return with(e -> e.clazz != null && e.clazz.equals(c) && e.name().endsWith("WRITE"));
  }

  public static PermissionKey[] with(Predicate<PermissionKey> keyPredicate) {
    return Arrays.stream(values()).filter(keyPredicate).toArray(PermissionKey[]::new);
  }

  public static String getPermissionHint(String permissionName) {
    ImmutableMap<String, String> permissionHints =
        ImmutableMap.<String, String>builder()
            .put("GO_UNDER_MIN", "Mindestguthaben unterschreiten")
            .put("ACTION_OPEN_MANAGE_PRICELISTS", "Preislisten bearbeiten")
            .put("ACTION_OPEN_SOLO_SHOPPING_MASK", "Selbsteinkauf")
            .put("ACTION_OPEN_APPLICATION_SETTINGS", "Programmeinstellungen bearbeiten")
            .put("ACTION_OPEN_PERMISSION_MANAGEMENT", "Berechtigungen bearbeiten")
            .put("ACTION_OPEN_PERMISSION_ASSIGNMENT", "Berechtigungen zuweisen")
            .put("ACTION_OPEN_SPECIAL_PRICE_EDITOR", "Aktionsartikel bearbeiten")
            .put("ACTION_OPEN_TRANSACTION", "Guthaben überweisen")
            .put("ACTION_OPEN_ACCOUNTING_REPORTS", "Berichte")
            .put("ACTION_OPEN_CASHIER_SHOPPING_MASK", "Ladendienst beginnen")
            .put("ACTION_OPEN_CHANGE_PASSWORD", "Passwort ändern")
            .put("ACTION_OPEN_EDIT_ARTICLES", "Artikel bearbeiten")
            .put("ACTION_OPEN_EDIT_JOBS", "Jobs bearbeiten")
            .put("ACTION_OPEN_EDIT_SUPPLIERS", "Lieferanten bearbeiten")
            .put("ACTION_OPEN_EDIT_SURCHARGE_GROUPS", "Zuschlaggruppen bearbeiten")
            .put("ACTION_OPEN_EDIT_USER_GROUP", "Nutzergruppe wechseln")
            .put("ACTION_OPEN_EDIT_USER_SETTING", "Persönliche Programmeinstellungen bearbeiten")
            .put("ACTION_OPEN_EDIT_USERS", "Benutzer bearbeiten")
            .put("ACTION_OPEN_MANAGE_PRICE_LISTS", "Preislisten bearbeiten")
            .put("ACTION_OPEN_PRE_ORDER", "Vorbestellung für alle öffnen")
            .put("ACTION_OPEN_OWN_PRE_ORDER", "Vorbestellung für mich öffnen")
            .put("ACTION_OPEN_SUPPLY", "Lieferung eingeben")
            .put("ACTION_OPEN_SYNCHRONISE_ARTICLE_WINDOW", "Kornkraft-Katalog synchronisieren")
            .put("ACTION_OPEN_DB_LOG_IN", "Datenverbindung bearbeiten")
            .put("ACTION_ADD_BEGINNER", "Probemitglied aufnehmen")
            .put("ACTION_EDIT_OWN_DATA", "Persönliche Informationen bearbeiten")
            .put("ACTION_LOGIN", "Anmelden")
            .put("ACTION_TRANSACTION_FROM_OTHER", "Überweisungen für andere tätigen")
            .put("ACTION_TRANSACTION_FROM_KB", "Überweisungen für Kernbeißer tätigen")
            .put("ACTION_ORDER_CONTAINER", "Vorbestellungen für alle bearbeiten")
            .put("ACTION_ORDER_OWN_CONTAINER", "Vorbestellungen für mich bearbeiten")
            .put("ACTION_OPEN_ADMIN_TOOLS", "Benutzer administrieren")
            .put("ACTION_GRANT_CASHIER_PERMISSION", "Ladendienstrolle vergeben")
            .build();
    return permissionHints.getOrDefault(permissionName, permissionName);
  }
}
