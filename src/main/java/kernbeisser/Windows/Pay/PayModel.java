package kernbeisser.Windows.Pay;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Collection;

class PayModel implements Model {
    private final SaleSession saleSession;
    private final Collection<ShoppingItem> shoppingCart;
    private final Runnable transferCompleted;
    PayModel(SaleSession saleSession,Collection<ShoppingItem> shoppingCart,Runnable transferCompleted){
        this.shoppingCart=shoppingCart;
        this.saleSession = saleSession;
        this.transferCompleted=transferCompleted;
    }

    Collection<ShoppingItem> getShoppingCart() {
        return shoppingCart;
    }

    SaleSession getSaleSession() {
        return saleSession;
    }

    int shoppingCartSum(){
        return shoppingCart.stream().mapToInt(ShoppingItem::getRawPrice).sum();
    }
    boolean pay(SaleSession saleSession, Collection<ShoppingItem> items, int sum){
        //Build connection by default
        EntityManager em = DBConnection.getEntityManager();

        //Start transaction
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            //Create saleSession if not exists
            SaleSession db = em.find(SaleSession.class,saleSession.getId());
            if(db==null){
                em.persist(saleSession);
                db=saleSession;
            }

            //Save ShoppingItems in Purchase
            Purchase purchase = new Purchase();
            purchase.setSession(db);
            em.persist(purchase);
            items.forEach(e -> {
                e.setPurchase(purchase);
                em.persist(e);
            });

            //Change value from UserGroup
            UserGroup userGroup = em.find(UserGroup.class, db.getCustomer().getUserGroup().getId());
            userGroup.setValue(userGroup.getValue() - sum);
            em.persist(userGroup);

            //Persist changes
            et.commit();

            //Close EntityManager
            em.close();

            //Success
            return true;
        }catch (PersistenceException e){
            e.printStackTrace();

            //Undo transaction
            et.rollback();

            //Close EntityManager
            em.close();

            //Failed
            return false;
        }
    }
    void runTransferCompleted(){
        transferCompleted.run();
    }

    PrintService[] getAllPrinters(){
        return PrintServiceLookup.lookupPrintServices(null,null);
    }

    void print(PrintService printService){
        try {
            //Creates new PrinterJob
            PrinterJob p =  PrinterJob.getPrinterJob();

            //Sets selected PrintService
            p.setPrintService(printService);

            
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    PrintService getDefaultPrinter(){
        return PrintServiceLookup.lookupDefaultPrintService();
    }
}
