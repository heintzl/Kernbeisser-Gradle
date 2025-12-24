package kernbeisser.Reports;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportFileNames {
  private static final String fileNameTemplate = "%s.jrxml";
  public static final String ARTICLE_LABEL_REPORT_FILENAME =
      fileNameTemplate.formatted("Etiketten");
  public static final String ACCOUNTING_REPORT_FILENAME =
      fileNameTemplate.formatted("BuchhaltungUmsaetze");
  public static final String INVENTORY_COUNTING_LISTS_REPORT_FILENAME =
      fileNameTemplate.formatted("Inventur_Zaehlliste");
  public static final String INVOICE_REPORT_FILENAME = fileNameTemplate.formatted("Kerni_Rechnung");
  public static final String KEY_USER_LIST_REPORT_FILENAME =
      fileNameTemplate.formatted("BenutzerSchluessel");
  public static final String PREORDER_CHECKLIST_REPORT_FILENAME =
      fileNameTemplate.formatted("Abhakplan");
  public static final String PRICELIST_REPORT_FILENAME = fileNameTemplate.formatted("Preisliste");
  public static final String TRANSACTION_STATEMENT_REPORT_FILENAME =
      fileNameTemplate.formatted("Kontoauszug");
  public static final String TRIAL_MEMBER_REPORT_FILENAME =
      fileNameTemplate.formatted("Probemitglieder");
  public static final String USER_BALANCE_REPORT_FILENAME =
      fileNameTemplate.formatted("GuthabenUebersicht");
  public static final String TILLROLL_REPORT_FILENAME = fileNameTemplate.formatted("Bonrolle");
  public static final String LOGININFO_REPORT_FILENAME =
      fileNameTemplate.formatted("LoginInformation");
  public static final String PERMISSION_HOLDERS_REPORT_FILENAME =
      fileNameTemplate.formatted("RollenInhaber");
  public static final String LOSSANALYSIS_REPORT_FILENAME =
      fileNameTemplate.formatted("Schwundanalyse");
  public static final String INVENTORY_SHELF_DETAILS_REPORT_FILENAME =
      fileNameTemplate.formatted("Inventur_Regaldetails");
  public static final String INVENTORY_SHELF_OVERVIEW_REPORT_FILENAME =
      fileNameTemplate.formatted("Inventur_Regaluebersicht");
  public static final String INVENTORY_SHELF_STOCKS =
      fileNameTemplate.formatted("Inventur_Regal_Bestaende");
  public static final String INVENTORY_STOCKS = fileNameTemplate.formatted("Inventur_Bestaende");
  public static final String PRODUCE_PRICELIST = fileNameTemplate.formatted("Gemuese_Preisliste");
  public static final String DELIVERY_BILL_REPORT_FILENAME =
      fileNameTemplate.formatted("VB_Lieferschein");
}
