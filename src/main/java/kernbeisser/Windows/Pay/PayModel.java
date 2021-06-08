package kernbeisser.Windows.Pay;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
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
        ShoppingItem.getSum(sumType, shoppingCart, ShoppingItem::isSolidaritySurcharged)
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
    shoppingCart.removeIf(ShoppingItem::isSolidaritySurchargeItem);
  }

  private void exchangeMoney(EntityManager em) throws InvalidTransactionException {
    Transaction transaction =
        Transaction.doPurchaseTransaction(em, saleSession.getCustomer(), shoppingCartSum());
    saleSession.setTransaction(transaction);
  }

  private void persistShoppingCartAndRetifyIndexes(EntityManager em, Purchase purchase) {
    int i = 0;
    for (ShoppingItem item : shoppingCart) {
      item.setShoppingCartIndex(i++);
      item.setPurchase(purchase);
      em.persist(item);
    }
  }

  long pay() throws PersistenceException, InvalidTransactionException {
    // Build connection to DB and start payment transaction
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      exchangeMoney(em);
      Purchase purchase = new Purchase();
      purchase.setSession(saleSession);
      em.persist(saleSession);
      persistShoppingCartAndRetifyIndexes(em, purchase);
      em.persist(purchase);
      et.commit();
      return purchase.getId();
    } finally {
      // roles back any made changes when the payment was interrupted
      // only happens if the code doesn't reach the commit statement
      if (et.isActive()) {
        et.rollback();
      }
    }
  }

  void runTransferCompleted() {

    transferCompleted.run();
  }

  public static void print(long purchaseId) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    new InvoiceReport(em.find(Purchase.class, purchaseId))
        .sendToPrinter("Bon wird erstellt", Tools::showUnexpectedErrorWarning);
  }

  public void safeStandardPrint(boolean printReceipt) {
    UserSetting.PRINT_RECEIPT.setValue(saleSession.getCustomer(), printReceipt);
  }

  public boolean readStandardPrint() {
    return UserSetting.PRINT_RECEIPT
        .getValue(saleSession.getCustomer())
        .equals(Boolean.TRUE.toString());
  }
}
