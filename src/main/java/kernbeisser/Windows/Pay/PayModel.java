package kernbeisser.Windows.Pay;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;
import net.sf.jasperreports.engine.JRException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.List;

import static kernbeisser.Reports.ReportUtil.exportInvoicePDF;

public class PayModel implements Model<PayController> {
    private final SaleSession saleSession;
    private final List<ShoppingItem> shoppingCart;
    private final Runnable transferCompleted;

    PayModel(SaleSession saleSession, List<ShoppingItem> shoppingCart, Runnable transferCompleted) {
        this.shoppingCart = rebuildIndex(shoppingCart);
        this.saleSession = saleSession;
        this.transferCompleted = transferCompleted;
    }

    List<ShoppingItem> getShoppingCart() {
        return shoppingCart;
    }

    List<ShoppingItem> rebuildIndex(List<ShoppingItem> cart) {
        cart.forEach((item) -> {
            item.setShoppingCartIndex(cart.indexOf(item));
        });
        return cart;
    }

    SaleSession getSaleSession() {
        return saleSession;
    }

    double shoppingCartSum() {
        return shoppingCart.stream()
                           .mapToDouble(ShoppingItem::getRetailPrice)
                           .sum() * (1 + saleSession.getCustomer().getSolidaritySurcharge());
    }

    Purchase pay(SaleSession saleSession, List<ShoppingItem> items, double sum)
            throws PersistenceException, AccessDeniedException {
        //Build connection by default
        EntityManager em = DBConnection.getEntityManager();

        //Start transaction
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            //Do money exchange
            try {
                Transaction.doPurchaseTransaction(saleSession.getCustomer(), shoppingCartSum());
            }catch (AccessDeniedException e) {
                em.close();
                throw new AccessDeniedException("The user has not enough value to buy these Articles");
            }

            //Create saleSession if not exists
            SaleSession db = em.find(SaleSession.class, saleSession.getId());
            if (db == null) {
                em.persist(saleSession);
                db = saleSession;
            }


            //Save ShoppingItems in Purchase
            Purchase purchase = new Purchase();
            purchase.setSession(db);
            em.persist(purchase);
            for (ShoppingItem item : items) {
                ShoppingItem shoppingItem = item.newInstance();
                shoppingItem.setPurchase(purchase);
                em.persist(shoppingItem);
            }

            //Persist changes
            et.commit();

            //Success
            return purchase;
        } finally {
            //Undo transaction
            if (et.isActive()) {
                et.rollback();
            }

            //Close EntityManager
            em.close();
        }
    }

    void runTransferCompleted() {
        transferCompleted.run();
    }

    PrintService[] getAllPrinters() {
        return PrintServiceLookup.lookupPrintServices(null, null);
    }

    void print(Purchase purchase, PrintService printService) {
        try {
            exportInvoicePDF(shoppingCart, purchase);
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
}
