package kernbeisser.Enums;

import java.util.ArrayList;
import java.util.Collection;

public enum Key {
    //Actions
    ACTION_TRANSACTION(KeyCategory.ACTION,Security.LOW),
    ACTION_TRANSACTION_FROM_OTHER(KeyCategory.ACTION,Security.EXTREME),
    ACTION_TRANSACTION_FROM_KB(KeyCategory.ACTION,Security.EXTREME),

    //DataBase Changes:
    USER_ID_READ(KeyCategory.USERS,Security.EXTREME),
    USER_ID_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_SALES_THIS_YEAR_READ(KeyCategory.USERS,Security.LOW),
    USER_SALES_THIS_YEAR_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_SALES_LAST_YEAR_READ(KeyCategory.USERS,Security.LOW),
    USER_SALES_LAST_YEAR_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_SHARES_READ(KeyCategory.USERS,Security.LOW),
    USER_SHARES_WRITE(KeyCategory.USERS,Security.HIGH),
    USER_SOLIDARITY_SURCHARGE_READ(KeyCategory.USERS,Security.LOW),
    USER_SOLIDARITY_SURCHARGE_WRITE(KeyCategory.USERS,Security.HIGH),
    USER_EXTRA_JOBS_READ(KeyCategory.USERS,Security.LOW),
    USER_EXTRA_JOBS_WRITE(KeyCategory.USERS,Security.MIDDLE),
    USER_JOBS_READ(KeyCategory.USERS,Security.LOW),
    USER_JOBS_WRITE(KeyCategory.USERS,Security.LOW),
    USER_LAST_BUY_READ(KeyCategory.USERS,Security.EXTREME),
    USER_LAST_BUY_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_KERNBEISSER_KEY_READ(KeyCategory.USERS,Security.EXTREME),
    USER_KERNBEISSER_KEY_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_EMPLOYEE_READ(KeyCategory.USERS,Security.EXTREME),
    USER_EMPLOYEE_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_USERNAME_READ(KeyCategory.USERS,Security.EXTREME),
    USER_USERNAME_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_PASSWORD_READ(KeyCategory.USERS,Security.EXTREME),
    USER_PASSWORD_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_FIRST_NAME_READ(KeyCategory.USERS,Security.EXTREME),
    USER_FIRST_NAME_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_SURNAME_READ(KeyCategory.USERS,Security.EXTREME),
    USER_SURNAME_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_PHONE_NUMBER1_READ(KeyCategory.USERS,Security.EXTREME),
    USER_PHONE_NUMBER1_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_PHONE_NUMBER2_READ(KeyCategory.USERS,Security.EXTREME),
    USER_PHONE_NUMBER2_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_STREET_READ(KeyCategory.USERS,Security.EXTREME),
    USER_STREET_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_TOWN_READ(KeyCategory.USERS,Security.EXTREME),
    USER_TOWN_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_TOWN_CODE_READ(KeyCategory.USERS,Security.EXTREME),
    USER_TOWN_CODE_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_PERMISSION_READ(KeyCategory.USERS,Security.EXTREME),
    USER_PERMISSION_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_EMAIL_READ(KeyCategory.USERS,Security.EXTREME),
    USER_EMAIL_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_CREATE_DATE_READ(KeyCategory.USERS,Security.EXTREME),
    USER_CREATE_DATE_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_UPDATE_DATE_READ(KeyCategory.USERS,Security.EXTREME),
    USER_UPDATE_DATE_WRITE(KeyCategory.USERS,Security.EXTREME),
    USER_USER_GROUP_READ(KeyCategory.USERS,Security.EXTREME),
    USER_USER_GROUP_WRITE(KeyCategory.USERS,Security.EXTREME),
    ITEM_IID_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_IID_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_NAME_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_NAME_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_KB_NUMBER_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_KB_NUMBER_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_AMOUNT_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_AMOUNT_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SURCHARGE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SURCHARGE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_NET_PRICE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_NET_PRICE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SUPPLIER_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SUPPLIER_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_BARCODE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_BARCODE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SPECIAL_PRICE_NET_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SPECIAL_PRICE_NET_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_VAT_LOW_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_VAT_LOW_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SINGLE_DEPOSIT_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SINGLE_DEPOSIT_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_CRATE_DEPOSIT_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_CRATE_DEPOSIT_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_UNIT_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_UNIT_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_PRICE_LIST_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_PRICE_LIST_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_CONTAINER_DEF_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_CONTAINER_DEF_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_CONTAINER_SIZE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_CONTAINER_SIZE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SUPPLIERS_ITEM_NUMBER_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SUPPLIERS_ITEM_NUMBER_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_WEIGHABLE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_WEIGHABLE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_LISTED_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_LISTED_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SHOW_IN_SHOP_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SHOW_IN_SHOP_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELETED_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELETED_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_PRINT_AGAIN_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_PRINT_AGAIN_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELETE_ALLOWED_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELETE_ALLOWED_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_LOSS_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_LOSS_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_INFO_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_INFO_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SOLD_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SOLD_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_SPECIAL_PRICE_MONTH_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_SPECIAL_PRICE_MONTH_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELIVERED_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELIVERED_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_INV_SHELF_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_INV_SHELF_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_INV_STOCK_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_INV_STOCK_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_INV_PRICE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_INV_PRICE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_INTAKE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_INTAKE_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_LAST_BUY_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_LAST_BUY_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_LAST_DELIVERY_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_LAST_DELIVERY_WRITE(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELETED_DATE_READ(KeyCategory.ITEMS,Security.LOW),
    ITEM_DELETED_DATE_WRITE(KeyCategory.ITEMS,Security.LOW),
    PRICELIST_PID_READ(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_PID_WRITE(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_NAME_READ(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_NAME_WRITE(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_SUPER_PRICE_LIST_READ(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_SUPER_PRICE_LIST_WRITE(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_UPDATE_DATE_READ(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_UPDATE_DATE_WRITE(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_CREATE_DATE_READ(KeyCategory.PRICE_LISTS,Security.LOW),
    PRICELIST_CREATE_DATE_WRITE(KeyCategory.PRICE_LISTS,Security.LOW),
    SUPPLIER_SID_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_SID_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_NAME_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_NAME_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_PHONE_NUMBER_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_PHONE_NUMBER_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_FAX_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_FAX_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_ADDRESS_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_ADDRESS_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_EMAIL_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_EMAIL_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_SHORT_NAME_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_SHORT_NAME_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_SURCHARGE_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_SURCHARGE_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_KEEPER_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_KEEPER_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_CREATE_DATE_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_CREATE_DATE_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_UPDATE_DATE_READ(KeyCategory.SUPPLIERS,Security.LOW),
    SUPPLIER_UPDATE_DATE_WRITE(KeyCategory.SUPPLIERS,Security.LOW),
    CONTAINER_ID_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_ID_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_ITEM_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_ITEM_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_USER_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_USER_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_INFO_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_INFO_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_AMOUNT_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_AMOUNT_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_NET_PRICE_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_NET_PRICE_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_PAYED_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_PAYED_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_DELIVERY_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_DELIVERY_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_CREATE_DATE_READ(KeyCategory.CONTAINERS,Security.LOW),
    CONTAINER_CREATE_DATE_WRITE(KeyCategory.CONTAINERS,Security.LOW),
    ITEM_KK_ID_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_ID_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_NAME_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_NAME_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_PRODUCER_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_PRODUCER_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_NET_PRICE_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_NET_PRICE_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_UNIT_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_UNIT_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_KK_NUMBER_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_KK_NUMBER_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_VAT_LOW_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_VAT_LOW_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_AMOUNT_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_AMOUNT_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_BARCODE_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_BARCODE_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_CONTAINER_SIZE_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_CONTAINER_SIZE_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_SINGLE_DEPOSIT_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_SINGLE_DEPOSIT_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_CRATE_DEPOSIT_READ(KeyCategory.ITEM_KK,Security.LOW),
    ITEM_KK_CRATE_DEPOSIT_WRITE(KeyCategory.ITEM_KK,Security.LOW),
    TRANSACTION_ID_WRITE(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_VALUE_READ(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_VALUE_WRITE(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_FROM_READ(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_FROM_WRITE(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_TO_READ(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_TO_WRITE(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_DATE_READ(KeyCategory.TRANSACTION,Security.MIDDLE),
    TRANSACTION_DATE_WRITE(KeyCategory.TRANSACTION,Security.MIDDLE),
    USER_GROUP_GID_READ(KeyCategory.USER_GROUP,Security.MIDDLE),
    USER_GROUP_GID_WRITE(KeyCategory.USER_GROUP,Security.MIDDLE),
    USER_GROUP_VALUE_READ(KeyCategory.USER_GROUP,Security.MIDDLE),
    USER_GROUP_VALUE_WRITE(KeyCategory.USER_GROUP,Security.MIDDLE),
    USER_GROUP_INTEREST_THIS_YEAR_READ(KeyCategory.USER_GROUP,Security.MIDDLE),
    USER_GROUP_INTEREST_THIS_YEAR_WRITE(KeyCategory.USER_GROUP,Security.MIDDLE),
    SHOPPING_ITEM_SIID_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SIID_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_AMOUNT_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_AMOUNT_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_DISCOUNT_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_DISCOUNT_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_PURCHASE_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_PURCHASE_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_NAME_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_NAME_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_KB_NUMBER_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_KB_NUMBER_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_ITEM_AMOUNT_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_ITEM_AMOUNT_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_ITEM_NET_PRICE_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_ITEM_NET_PRICE_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_VAT_LOW_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_VAT_LOW_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_UNIT_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_UNIT_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_WEIGH_ABLE_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_WEIGH_ABLE_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SHORT_NAME_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SHORT_NAME_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SURCHARGE_READ(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SHOPPING_ITEM_SURCHARGE_WRITE(KeyCategory.SHOPPING_ITEMS,Security.MIDDLE),
    SURCHARGE_TABLE_STID_READ(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_STID_WRITE(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_SURCHARGE_READ(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_SURCHARGE_WRITE(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_FROM_READ(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_FROM_WRITE(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_TO_READ(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_TO_WRITE(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_NAME_READ(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_NAME_WRITE(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_SUPPLIER_READ(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SURCHARGE_TABLE_SUPPLIER_WRITE(KeyCategory.SURCHARGE_TABLE,Security.LOW),
    SHELF_ID_READ(KeyCategory.SHELVES,Security.LOW),
    SHELF_ID_WRITE(KeyCategory.SHELVES,Security.LOW),
    SHELF_PRICE_LISTS_READ(KeyCategory.SHELVES,Security.LOW),
    SHELF_PRICE_LISTS_WRITE(KeyCategory.SHELVES,Security.LOW),
    SHELF_NOTE_READ(KeyCategory.SHELVES,Security.LOW),
    SHELF_NOTE_WRITE(KeyCategory.SHELVES,Security.LOW),
    PURCHASE_SID_READ(KeyCategory.PURCHASE,Security.HIGH),
    PURCHASE_SID_WRITE(KeyCategory.PURCHASE,Security.HIGH),
    PURCHASE_SESSION_READ(KeyCategory.PURCHASE,Security.HIGH),
    PURCHASE_SESSION_WRITE(KeyCategory.PURCHASE,Security.HIGH),
    PURCHASE_CREATE_DATE_READ(KeyCategory.PURCHASE,Security.HIGH),
    PURCHASE_CREATE_DATE_WRITE(KeyCategory.PURCHASE,Security.HIGH),
    JOB_JID_READ(KeyCategory.JOBS,Security.LOW),
    JOB_JID_WRITE(KeyCategory.JOBS,Security.LOW),
    JOB_NAME_READ(KeyCategory.JOBS,Security.LOW),
    JOB_NAME_WRITE(KeyCategory.JOBS,Security.LOW),
    JOB_DESCRIPTION_READ(KeyCategory.JOBS,Security.LOW),
    JOB_DESCRIPTION_WRITE(KeyCategory.JOBS,Security.LOW),
    JOB_CREATE_DATE_READ(KeyCategory.JOBS,Security.LOW),
    JOB_CREATE_DATE_WRITE(KeyCategory.JOBS,Security.LOW),
    JOB_UPDATE_DATE_READ(KeyCategory.JOBS,Security.LOW),
    JOB_UPDATE_DATE_WRITE(KeyCategory.JOBS,Security.LOW),
    PERMISSION_ID_READ(KeyCategory.PERMISSIONS,Security.EXTREME),
    PERMISSION_ID_WRITE(KeyCategory.PERMISSIONS,Security.EXTREME),
    PERMISSION_NAME_READ(KeyCategory.PERMISSIONS,Security.EXTREME),
    PERMISSION_NAME_WRITE(KeyCategory.PERMISSIONS,Security.EXTREME),
    PERMISSION_KEY_SET_READ(KeyCategory.PERMISSIONS,Security.EXTREME),
    PERMISSION_KEY_SET_WRITE(KeyCategory.PERMISSIONS,Security.EXTREME),
    SALE_SESSION_S_SID_READ(KeyCategory.SALE_SESSION,Security.EXTREME),
    SALE_SESSION_S_SID_WRITE(KeyCategory.SALE_SESSION,Security.EXTREME),
    SALE_SESSION_CUSTOMER_READ(KeyCategory.SALE_SESSION,Security.EXTREME),
    SALE_SESSION_CUSTOMER_WRITE(KeyCategory.SALE_SESSION,Security.EXTREME),
    SALE_SESSION_SELLER_READ(KeyCategory.SALE_SESSION,Security.EXTREME),
    SALE_SESSION_SELLER_WRITE(KeyCategory.SALE_SESSION,Security.EXTREME),
    ;

    private final KeyCategory category;
    private final Security security;
    Key(KeyCategory category,Security security){
        this.security=security;
        this.category=category;
    }

    public static Collection<Key> find(KeyCategory category){
        Collection<Key> out = new ArrayList<>();
        for (Key value : values()) {
            if(value.category==category)
                out.add(value);
        }
        return out;
    }

    public static Collection<Key> find(KeyCategory category, Security security){
        Collection<Key> out = new ArrayList<>();
        for (Key value : values()) {
            if(value.category==category&&security==value.security)
                out.add(value);
        }
        return out;
    }
}
