package kernbeisser.Windows.Pay;

import java.awt.event.ActionListener;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Enums.ShoppingItemSum;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Reports.InvoiceReport;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class PayModel implements IModel<PayController> {
  private boolean successful = false;
  private final SaleSession saleSession;
  private final List<ShoppingItem> shoppingCart;
  private final Runnable transferCompleted;

  PayModel(SaleSession saleSession, List<ShoppingItem> shoppingCart, Runnable transferCompleted) {
    this.shoppingCart = shoppingCart;
    this.saleSession = saleSession;
    this.transferCompleted = transferCompleted;
    double solidaritySurcharge = saleSession.getCustomer().getUserGroup().getSolidaritySurcharge();
    if (solidaritySurcharge > 0.0) addSolidarityItems(solidaritySurcharge);
  }

  private void addSolidarityItems(double solidaritySurcharge) {
    removeSolidarityItems();
    addSolidarityItem(VAT.LOW, solidaritySurcharge);
    addSolidarityItem(VAT.HIGH, solidaritySurcharge);
  }

  private void addSolidarityItem(VAT vat, double solidaritySurcharge) {
    ShoppingItemSum sumType = ShoppingItemSum.RETAILPRICE_VATHIGH;
    if (vat == VAT.LOW) {
      sumType = ShoppingItemSum.RETAILPRICE_VATLOW;
    }
    double value =
        ShoppingItem.getSum(
                sumType, shoppingCart, s -> s.getParentItem() == null && !s.isSolidaritySurcharge())
            * solidaritySurcharge;
    shoppingCart.add(ShoppingItem.createSolidaritySurcharge(value, vat, solidaritySurcharge));
  }

  List<ShoppingItem> getShoppingCart() {
    return shoppingCart;
  }

  SaleSession getSaleSession() {
    return saleSession;
  }

  double shoppingCartSum() {
    return shoppingCart.stream().mapToDouble(ShoppingItem::getRetailPrice).sum();
  }

  void removeSolidarityItems() {
    shoppingCart.removeIf(ShoppingItem::isSolidaritySurcharge);
  }

  Purchase pay() throws PersistenceException, InvalidTransactionException {
    // Build connection by default
    @Cleanup EntityManager em = DBConnection.getEntityManager();

    // Start transaction
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      // Do money exchange
      try {
        Transaction transaction =
            Transaction.doPurchaseTransaction(em, saleSession.getCustomer(), shoppingCartSum());
        saleSession.setTransaction(transaction);
      } catch (InvalidTransactionException e) {
        et.rollback();
        em.close();
        throw new InvalidTransactionException();
      }

      // Create saleSession if not exists
      SaleSession db = em.find(SaleSession.class, saleSession.getId());
      if (db == null) {
        em.persist(saleSession);
        db = saleSession;
      }

      // Save ShoppingItems in Purchase and rectify cart indices
      Purchase purchase = new Purchase();
      purchase.setSession(db);
      em.persist(purchase);
      int i = 0;
      for (ShoppingItem item : shoppingCart) {
        ShoppingItem shoppingItem = item.unproxy();
        shoppingItem.setShoppingCartIndex(i);
        shoppingItem.setPurchase(purchase);
        em.persist(shoppingItem);
        i++;
      }

      // Persist changes
      et.commit();

      // Success
      successful = true;
      return purchase;
    } finally {
      // Undo transaction
      if (et.isActive()) {
        et.rollback();
      }

      // Close EntityManager
      em.close();
    }
  }

  void runTransferCompleted() {

    transferCompleted.run();
  }

  PrintService[] getAllPrinters() {
    return PrintServiceLookup.lookupPrintServices(null, null);
  }

  public static void print(Purchase purchase) {
    InvoiceReport invoice = new InvoiceReport(purchase);
    invoice.sendToPrinter("Bon wird erstellt", (e) -> Tools.showUnexpectedErrorWarning(e));
  }

    public void safeStandardPrint(boolean printReceipt) {
      UserSetting.PRINT_RECEIPT.setValue(saleSession.getCustomer(),printReceipt);
    }

  public boolean readStandardPrint() {
    return UserSetting.PRINT_RECEIPT.getValue(saleSession.getCustomer()).equals(Boolean.TRUE.toString());
  }
}
