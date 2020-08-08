package kernbeisser.Windows.Pay;

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
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Reports.ReportManager;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Model;
import net.sf.jasperreports.engine.JRException;

public class PayModel implements Model<PayController> {
  private boolean successful = false;
  private final SaleSession saleSession;
  private final List<ShoppingItem> shoppingCart;
  private final Runnable transferCompleted;

  PayModel(SaleSession saleSession, List<ShoppingItem> shoppingCart, Runnable transferCompleted) {
    this.shoppingCart = shoppingCart;
    this.saleSession = saleSession;
    this.transferCompleted = transferCompleted;
  }

  List<ShoppingItem> getShoppingCart() {
    return shoppingCart;
  }

  SaleSession getSaleSession() {
    return saleSession;
  }

  double shoppingCartSum() {
    return shoppingCart.stream().mapToDouble(ShoppingItem::getRetailPrice).sum()
        * (1 + saleSession.getCustomer().getSolidaritySurcharge());
  }

  Purchase pay(SaleSession saleSession, List<ShoppingItem> items, double sum)
      throws PersistenceException, AccessDeniedException {
    // Build connection by default
    EntityManager em = DBConnection.getEntityManager();

    // Start transaction
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      // Do money exchange
      try {
        Transaction.doPurchaseTransaction(saleSession.getCustomer(), shoppingCartSum());
      } catch (AccessDeniedException e) {
        em.close();
        throw new AccessDeniedException("The user has not enough value to buy these Articles");
      }

      // Create saleSession if not exists
      SaleSession db = em.find(SaleSession.class, saleSession.getSSid());
      if (db == null) {
        em.persist(saleSession);
        db = saleSession;
      }

      // Save ShoppingItems in Purchase and rectify cart indices
      Purchase purchase = new Purchase();
      purchase.setSession(db);
      em.persist(purchase);
      int i = 0;
      for (ShoppingItem item : items) {
        ShoppingItem shoppingItem = item.newInstance();
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

  void print(Purchase purchase) {
    try {
      ReportManager invoice = new ReportManager();
      ReportManager.initInvoicePrint(shoppingCart, purchase);
      invoice.sendToPrinter();
    } catch (JRException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    //        try {
    //            //Creates new PrinterJob
    //            PrinterJob p = PrinterJob.getPrinterJob();
    //
    //            //Sets selected PrintService
    //            p.setPrintService(printService);
    //
    //
    //        } catch (PrinterException e) {
    //            Tools.showUnexpectedErrorWarning(e);
    //        }
  }

  PrintService getDefaultPrinter() {
    return PrintServiceLookup.lookupDefaultPrintService();
  }

  public boolean wasSuccessful() {
    return successful;
  }
}
