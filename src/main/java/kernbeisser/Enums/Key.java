package kernbeisser.Enums;

import kernbeisser.Windows.LogIn.LogInModel;

import java.util.ArrayList;
import java.util.Collection;

public enum Key {

    //Window Permissions
    ACTION_OPEN_MANAGE_PRICELISTS(KeyCategory.WINDOW),

    //Actions
    ACTION_LOGIN(KeyCategory.ACTION),
    ACTION_TRANSACTION(KeyCategory.ACTION),
    ACTION_TRANSACTION_FROM_OTHER(KeyCategory.ACTION),
    ACTION_TRANSACTION_FROM_KB(KeyCategory.ACTION),
    ACTION_EDIT_USER(KeyCategory.ACTION),
    ACTION_ORDER_CONTAINER(KeyCategory.ACTION),
    ACTION_EDIT_PRICELIST(KeyCategory.ACTION),
    ACTION_DELETE_PRICELIST(KeyCategory.ACTION),
    ACTION_ADD_PRICELIST(KeyCategory.ACTION),

    //DataBase Changes:
    USER_ID_READ(KeyCategory.USERS),
    USER_ID_WRITE(KeyCategory.USERS),
    USER_SALES_THIS_YEAR_READ(KeyCategory.USERS),
    USER_SALES_THIS_YEAR_WRITE(KeyCategory.USERS),
    USER_SALES_LAST_YEAR_READ(KeyCategory.USERS),
    USER_SALES_LAST_YEAR_WRITE(KeyCategory.USERS),
    USER_SHARES_READ(KeyCategory.USERS),
    USER_SHARES_WRITE(KeyCategory.USERS),
    USER_SOLIDARITY_SURCHARGE_READ(KeyCategory.USERS),
    USER_SOLIDARITY_SURCHARGE_WRITE(KeyCategory.USERS),
    USER_EXTRA_JOBS_READ(KeyCategory.USERS),
    USER_EXTRA_JOBS_WRITE(KeyCategory.USERS),
    USER_JOBS_READ(KeyCategory.USERS),
    USER_JOBS_WRITE(KeyCategory.USERS),
    USER_LAST_BUY_READ(KeyCategory.USERS),
    USER_LAST_BUY_WRITE(KeyCategory.USERS),
    USER_KERNBEISSER_KEY_READ(KeyCategory.USERS),
    USER_KERNBEISSER_KEY_WRITE(KeyCategory.USERS),
    USER_EMPLOYEE_READ(KeyCategory.USERS),
    USER_EMPLOYEE_WRITE(KeyCategory.USERS),
    USER_USERNAME_READ(KeyCategory.USERS),
    USER_USERNAME_WRITE(KeyCategory.USERS),
    USER_PASSWORD_READ(KeyCategory.USERS),
    USER_PASSWORD_WRITE(KeyCategory.USERS),
    USER_FIRST_NAME_READ(KeyCategory.USERS),
    USER_FIRST_NAME_WRITE(KeyCategory.USERS),
    USER_SURNAME_READ(KeyCategory.USERS),
    USER_SURNAME_WRITE(KeyCategory.USERS),
    USER_PHONE_NUMBER1_READ(KeyCategory.USERS),
    USER_PHONE_NUMBER1_WRITE(KeyCategory.USERS),
    USER_PHONE_NUMBER2_READ(KeyCategory.USERS),
    USER_PHONE_NUMBER2_WRITE(KeyCategory.USERS),
    USER_STREET_READ(KeyCategory.USERS),
    USER_STREET_WRITE(KeyCategory.USERS),
    USER_TOWN_READ(KeyCategory.USERS),
    USER_TOWN_WRITE(KeyCategory.USERS),
    USER_TOWN_CODE_READ(KeyCategory.USERS),
    USER_TOWN_CODE_WRITE(KeyCategory.USERS),
    USER_PERMISSION_READ(KeyCategory.USERS),
    USER_PERMISSION_WRITE(KeyCategory.USERS),
    USER_EMAIL_READ(KeyCategory.USERS),
    USER_EMAIL_WRITE(KeyCategory.USERS),
    USER_CREATE_DATE_READ(KeyCategory.USERS),
    USER_CREATE_DATE_WRITE(KeyCategory.USERS),
    USER_UPDATE_DATE_READ(KeyCategory.USERS),
    USER_UPDATE_DATE_WRITE(KeyCategory.USERS),
    USER_USER_GROUP_READ(KeyCategory.USERS),
    USER_USER_GROUP_WRITE(KeyCategory.USERS),
    ARTICLE_IID_READ(KeyCategory.ARTICLE),
    ARTICLE_IID_WRITE(KeyCategory.ARTICLE),
    ARTICLE_NAME_READ(KeyCategory.ARTICLE),
    ARTICLE_NAME_WRITE(KeyCategory.ARTICLE),
    ARTICLE_KB_NUMBER_READ(KeyCategory.ARTICLE),
    ARTICLE_KB_NUMBER_WRITE(KeyCategory.ARTICLE),
    ARTICLE_AMOUNT_READ(KeyCategory.ARTICLE),
    ARTICLE_AMOUNT_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SURCHARGE_READ(KeyCategory.ARTICLE),
    ARTICLE_SURCHARGE_WRITE(KeyCategory.ARTICLE),
    ARTICLE_NET_PRICE_READ(KeyCategory.ARTICLE),
    ARTICLE_NET_PRICE_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SUPPLIER_READ(KeyCategory.ARTICLE),
    ARTICLE_SUPPLIER_WRITE(KeyCategory.ARTICLE),
    ARTICLE_BARCODE_READ(KeyCategory.ARTICLE),
    ARTICLE_BARCODE_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SPECIAL_PRICE_NET_READ(KeyCategory.ARTICLE),
    ARTICLE_SPECIAL_PRICE_NET_WRITE(KeyCategory.ARTICLE),
    ARTICLE_VAT_READ(KeyCategory.ARTICLE),
    ARTICLE_VAT_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SINGLE_DEPOSIT_READ(KeyCategory.ARTICLE),
    ARTICLE_SINGLE_DEPOSIT_WRITE(KeyCategory.ARTICLE),
    ARTICLE_CRATE_DEPOSIT_READ(KeyCategory.ARTICLE),
    ARTICLE_CRATE_DEPOSIT_WRITE(KeyCategory.ARTICLE),
    ARTICLE_METRIC_UNITS_READ(KeyCategory.ARTICLE),
    ARTICLE_METRIC_UNITS_WRITE(KeyCategory.ARTICLE),
    ARTICLE_PRICE_LIST_READ(KeyCategory.ARTICLE),
    ARTICLE_PRICE_LIST_WRITE(KeyCategory.ARTICLE),
    ARTICLE_CONTAINER_DEF_READ(KeyCategory.ARTICLE),
    ARTICLE_CONTAINER_DEF_WRITE(KeyCategory.ARTICLE),
    ARTICLE_CONTAINER_SIZE_READ(KeyCategory.ARTICLE),
    ARTICLE_CONTAINER_SIZE_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SUPPLIERS_ITEM_NUMBER_READ(KeyCategory.ARTICLE),
    ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE(KeyCategory.ARTICLE),
    ARTICLE_WEIGHABLE_READ(KeyCategory.ARTICLE),
    ARTICLE_WEIGHABLE_WRITE(KeyCategory.ARTICLE),
    ARTICLE_LISTED_READ(KeyCategory.ARTICLE),
    ARTICLE_LISTED_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SHOW_IN_SHOP_READ(KeyCategory.ARTICLE),
    ARTICLE_SHOW_IN_SHOP_WRITE(KeyCategory.ARTICLE),
    ARTICLE_DELETED_READ(KeyCategory.ARTICLE),
    ARTICLE_DELETED_WRITE(KeyCategory.ARTICLE),
    ARTICLE_PRINT_AGAIN_READ(KeyCategory.ARTICLE),
    ARTICLE_PRINT_AGAIN_WRITE(KeyCategory.ARTICLE),
    ARTICLE_DELETE_ALLOWED_READ(KeyCategory.ARTICLE),
    ARTICLE_DELETE_ALLOWED_WRITE(KeyCategory.ARTICLE),
    ARTICLE_INFO_READ(KeyCategory.ARTICLE),
    ARTICLE_INFO_WRITE(KeyCategory.ARTICLE),
    ARTICLE_SPECIAL_PRICE_MONTH_READ(KeyCategory.ARTICLE),
    ARTICLE_SPECIAL_PRICE_MONTH_WRITE(KeyCategory.ARTICLE),
    ARTICLE_DELIVERED_READ(KeyCategory.ARTICLE),
    ARTICLE_DELIVERED_WRITE(KeyCategory.ARTICLE),
    ARTICLE_INTAKE_READ(KeyCategory.ARTICLE),
    ARTICLE_INTAKE_WRITE(KeyCategory.ARTICLE),
    ARTICLE_LAST_BUY_READ(KeyCategory.ARTICLE),
    ARTICLE_LAST_BUY_WRITE(KeyCategory.ARTICLE),
    ARTICLE_LAST_DELIVERY_READ(KeyCategory.ARTICLE),
    ARTICLE_LAST_DELIVERY_WRITE(KeyCategory.ARTICLE),
    ARTICLE_DELETED_DATE_READ(KeyCategory.ARTICLE),
    ARTICLE_DELETED_DATE_WRITE(KeyCategory.ARTICLE),
    PRICELIST_PID_READ(KeyCategory.PRICE_LISTS),
    PRICELIST_PID_WRITE(KeyCategory.PRICE_LISTS),
    PRICELIST_NAME_READ(KeyCategory.PRICE_LISTS),
    PRICELIST_NAME_WRITE(KeyCategory.PRICE_LISTS),
    PRICELIST_SUPER_PRICE_LIST_READ(KeyCategory.PRICE_LISTS),
    PRICELIST_SUPER_PRICE_LIST_WRITE(KeyCategory.PRICE_LISTS),
    PRICELIST_UPDATE_DATE_READ(KeyCategory.PRICE_LISTS),
    PRICELIST_UPDATE_DATE_WRITE(KeyCategory.PRICE_LISTS),
    PRICELIST_CREATE_DATE_READ(KeyCategory.PRICE_LISTS),
    PRICELIST_CREATE_DATE_WRITE(KeyCategory.PRICE_LISTS),
    SUPPLIER_SID_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_SID_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_NAME_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_NAME_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_PHONE_NUMBER_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_PHONE_NUMBER_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_FAX_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_FAX_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_ADDRESS_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_ADDRESS_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_EMAIL_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_EMAIL_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_SHORT_NAME_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_SHORT_NAME_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_SURCHARGE_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_SURCHARGE_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_KEEPER_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_KEEPER_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_CREATE_DATE_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_CREATE_DATE_WRITE(KeyCategory.SUPPLIERS),
    SUPPLIER_UPDATE_DATE_READ(KeyCategory.SUPPLIERS),
    SUPPLIER_UPDATE_DATE_WRITE(KeyCategory.SUPPLIERS),
    CONTAINER_ID_READ(KeyCategory.CONTAINERS),
    CONTAINER_ID_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_ITEM_READ(KeyCategory.CONTAINERS),
    CONTAINER_ITEM_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_USER_READ(KeyCategory.CONTAINERS),
    CONTAINER_USER_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_INFO_READ(KeyCategory.CONTAINERS),
    CONTAINER_INFO_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_AMOUNT_READ(KeyCategory.CONTAINERS),
    CONTAINER_AMOUNT_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_NET_PRICE_READ(KeyCategory.CONTAINERS),
    CONTAINER_NET_PRICE_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_PAYED_READ(KeyCategory.CONTAINERS),
    CONTAINER_PAYED_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_DELIVERY_READ(KeyCategory.CONTAINERS),
    CONTAINER_DELIVERY_WRITE(KeyCategory.CONTAINERS),
    CONTAINER_CREATE_DATE_READ(KeyCategory.CONTAINERS),
    CONTAINER_CREATE_DATE_WRITE(KeyCategory.CONTAINERS),
    ARTICLE_KK_ID_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_ID_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_NAME_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_NAME_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_PRODUCER_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_PRODUCER_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_NET_PRICE_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_NET_PRICE_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_UNIT_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_UNIT_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_KK_NUMBER_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_KK_NUMBER_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_VAT_LOW_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_VAT_LOW_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_AMOUNT_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_AMOUNT_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_BARCODE_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_BARCODE_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_CONTAINER_SIZE_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_CONTAINER_SIZE_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_SINGLE_DEPOSIT_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_SINGLE_DEPOSIT_WRITE(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_CRATE_DEPOSIT_READ(KeyCategory.ARTICLE_KK),
    ARTICLE_KK_CRATE_DEPOSIT_WRITE(KeyCategory.ARTICLE_KK),
    TRANSACTION_ID_WRITE(KeyCategory.TRANSACTION),
    TRANSACTION_VALUE_READ(KeyCategory.TRANSACTION),
    TRANSACTION_VALUE_WRITE(KeyCategory.TRANSACTION),
    TRANSACTION_FROM_READ(KeyCategory.TRANSACTION),
    TRANSACTION_FROM_WRITE(KeyCategory.TRANSACTION),
    TRANSACTION_TO_READ(KeyCategory.TRANSACTION),
    TRANSACTION_TO_WRITE(KeyCategory.TRANSACTION),
    TRANSACTION_DATE_READ(KeyCategory.TRANSACTION),
    TRANSACTION_DATE_WRITE(KeyCategory.TRANSACTION),
    USER_GROUP_GID_READ(KeyCategory.USER_GROUP),
    USER_GROUP_GID_WRITE(KeyCategory.USER_GROUP),
    USER_GROUP_VALUE_READ(KeyCategory.USER_GROUP),
    USER_GROUP_VALUE_WRITE(KeyCategory.USER_GROUP),
    USER_GROUP_INTEREST_THIS_YEAR_READ(KeyCategory.USER_GROUP),
    USER_GROUP_INTEREST_THIS_YEAR_WRITE(KeyCategory.USER_GROUP),
    SHOPPING_ITEM_SIID_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SIID_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_AMOUNT_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_AMOUNT_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_DISCOUNT_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_DISCOUNT_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_PURCHASE_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_PURCHASE_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_NAME_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_NAME_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_KB_NUMBER_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_KB_NUMBER_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_ITEM_AMOUNT_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_ITEM_AMOUNT_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_ITEM_NET_PRICE_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_ITEM_NET_PRICE_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_VAT_LOW_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_VAT_LOW_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_UNIT_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_UNIT_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_WEIGH_ABLE_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_WEIGH_ABLE_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SHORT_NAME_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SHORT_NAME_WRITE(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SURCHARGE_READ(KeyCategory.SHOPPING_ITEMS),
    SHOPPING_ITEM_SURCHARGE_WRITE(KeyCategory.SHOPPING_ITEMS),
    SURCHARGE_TABLE_STID_READ(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_STID_WRITE(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_SURCHARGE_READ(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_SURCHARGE_WRITE(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_FROM_READ(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_FROM_WRITE(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_TO_READ(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_TO_WRITE(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_NAME_READ(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_NAME_WRITE(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_SUPPLIER_READ(KeyCategory.SURCHARGE_TABLE),
    SURCHARGE_TABLE_SUPPLIER_WRITE(KeyCategory.SURCHARGE_TABLE),
    SHELF_ID_READ(KeyCategory.SHELVES),
    SHELF_ID_WRITE(KeyCategory.SHELVES),
    SHELF_PRICE_LISTS_READ(KeyCategory.SHELVES),
    SHELF_PRICE_LISTS_WRITE(KeyCategory.SHELVES),
    SHELF_NOTE_READ(KeyCategory.SHELVES),
    SHELF_NOTE_WRITE(KeyCategory.SHELVES),
    PURCHASE_SID_READ(KeyCategory.PURCHASE),
    PURCHASE_SID_WRITE(KeyCategory.PURCHASE),
    PURCHASE_SESSION_READ(KeyCategory.PURCHASE),
    PURCHASE_SESSION_WRITE(KeyCategory.PURCHASE),
    PURCHASE_CREATE_DATE_READ(KeyCategory.PURCHASE),
    PURCHASE_CREATE_DATE_WRITE(KeyCategory.PURCHASE),
    JOB_JID_READ(KeyCategory.JOBS),
    JOB_JID_WRITE(KeyCategory.JOBS),
    JOB_NAME_READ(KeyCategory.JOBS),
    JOB_NAME_WRITE(KeyCategory.JOBS),
    JOB_DESCRIPTION_READ(KeyCategory.JOBS),
    JOB_DESCRIPTION_WRITE(KeyCategory.JOBS),
    JOB_CREATE_DATE_READ(KeyCategory.JOBS),
    JOB_CREATE_DATE_WRITE(KeyCategory.JOBS),
    JOB_UPDATE_DATE_READ(KeyCategory.JOBS),
    JOB_UPDATE_DATE_WRITE(KeyCategory.JOBS),
    PERMISSION_ID_READ(KeyCategory.PERMISSIONS),
    PERMISSION_ID_WRITE(KeyCategory.PERMISSIONS),
    PERMISSION_NAME_READ(KeyCategory.PERMISSIONS),
    PERMISSION_NAME_WRITE(KeyCategory.PERMISSIONS),
    PERMISSION_KEY_SET_READ(KeyCategory.PERMISSIONS),
    PERMISSION_KEY_SET_WRITE(KeyCategory.PERMISSIONS),
    SALE_SESSION_S_SID_READ(KeyCategory.SALE_SESSION),
    SALE_SESSION_S_SID_WRITE(KeyCategory.SALE_SESSION),
    SALE_SESSION_CUSTOMER_READ(KeyCategory.SALE_SESSION),
    SALE_SESSION_CUSTOMER_WRITE(KeyCategory.SALE_SESSION),
    SALE_SESSION_SELLER_READ(KeyCategory.SALE_SESSION),
    SALE_SESSION_SELLER_WRITE(KeyCategory.SALE_SESSION);

    private final KeyCategory category;

    Key(KeyCategory category) {
        this.category = category;
    }

    public Key getWriteKey(){
        return valueOf(name().replace("READ","WRITE"));
    }

    public Key getReadKey(){
        return valueOf(name().replace("WRITE","READ"));
    }

    public boolean userHas(){
        return LogInModel.getLoggedIn().hasPermission(this);
    }

    public static Collection<Key> find(KeyCategory category) {
        Collection<Key> out = new ArrayList<>();
        for (Key value : values()) {
            if (value.category == category) {
                out.add(value);
            }
        }
        return out;
    }

    public static Collection<Key> find(KeyCategory category,boolean read,boolean write){
        Collection<Key> out = new ArrayList<>();
        for (Key value : values()) {
            if (value.category == category) {
                if((read&&value.name().endsWith("READ"))||(write&&value.name().endsWith("WRITE")))
                out.add(value);
            }
        }
        return out;
    }

}
