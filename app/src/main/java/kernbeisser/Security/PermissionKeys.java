package kernbeisser.Security;

import static rs.groump.PermissionKey.*;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import kernbeisser.Enums.Setting;
import lombok.Getter;
import rs.groump.PermissionKey;
import rs.groump.PermissionSet;

public class PermissionKeys {
  @Getter(lazy = true)
  private static final PermissionSet allActionPermissions =
      PermissionSet.asPermissionSet(PermissionKeyGroups.ACTIONS.getKeys());

  public static Collection<PermissionKey> find(
      PermissionKeyGroups permissionKeyGroups, boolean read, boolean write) {
    Collection<PermissionKey> out = new ArrayList<>();

    for (PermissionKey value : permissionKeyGroups.getKeys()) {
      if ((read && value.name().endsWith("READ")) || (write && value.name().endsWith("WRITE"))) {
        out.add(value);
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

  public static PermissionKey getWriteKey(PermissionKey readKey) {
    return PermissionKey.valueOf(readKey.name().replace("_READ", "_WRITE"));
  }

  public static PermissionKey getReadKey(PermissionKey writeKey) {
    return PermissionKey.valueOf(writeKey.name().replace("_WRITE", "_READ"));
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
            .put(
                "ACTION_OPEN_PERMISSION_GRANT_ASSIGNMENT",
                "Berechtigungen zur Berechtigungsweitergabe zuweisen")
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
            .put("ACTION_OPEN_INVETORY", "Inventur")
            .put("ACTION_OPEN_MANAGE_PRICE_LISTS", "Preislisten bearbeiten")
            .put("ACTION_OPEN_PRE_ORDER", "Vorbestellung für alle öffnen")
            .put("ACTION_OPEN_OWN_PRE_ORDER", "Vorbestellung für mich öffnen")
            .put("ACTION_OPEN_SUPPLY", "Lieferung eingeben")
            .put("ACTION_OPEN_SYNCHRONISE_ARTICLE_WINDOW", "Kornkraft-Katalog synchronisieren")
            .put("ACTION_OPEN_DB_LOG_IN", "Datenverbindung bearbeiten")
            .put("ACTION_ADD_TRIAL_MEMBER", "Probemitglied aufnehmen")
            .put("ACTION_EDIT_OWN_DATA", "Persönliche Informationen bearbeiten")
            .put("ACTION_LOGIN", "Anmelden")
            .put("ACTION_TRANSACTION_FROM_OTHER", "Überweisungen für andere tätigen")
            .put(
                "ACTION_TRANSACTION_FROM_KB",
                "Überweisungen für " + Setting.STORE_NAME.getStringValue() + " tätigen")
            .put("ACTION_ORDER_CONTAINER", "Vorbestellungen für alle bearbeiten")
            .put("ACTION_ORDER_OWN_CONTAINER", "Vorbestellungen für mich bearbeiten")
            .put("ACTION_OPEN_ADMIN_TOOLS", "Benutzer administrieren")
            .put("ACTION_GRANT_CASHIER_PERMISSION", "Ladendienstrolle vergeben")
            .put("ACTION_OPEN_PRINT_LABELS", "Etiketten drucken")
            .put("ACTION_OPEN_INVENTORY", "Inventur starten")
            .put("ACTION_OPEN_CATALOG_IMPORT", "Kornkraft Katalog importieren")
            .put("POST_ON_SALE_SESSION_CLOSE", "Nachricht bei LD-Abschluss bearbeiten")
            .put("POST_ON_SHOPPINGMASK_CHECKOUT", "Nachricht vor Bezahlen bearbeiten")
            .build();
    return permissionHints.getOrDefault(permissionName, permissionName);
  }
}
