package kernbeisser.Windows.ShoppingMask;

import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.RawPrice;
import kernbeisser.Enums.VAT;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Pay.PayView;

public class ShoppingMaskController implements Controller {
    private ShoppingMaskView view;
    private ShoppingMaskModel model;

    ShoppingMaskController(ShoppingMaskView view, SaleSession saleSession){
        this.view=view;
        model=new ShoppingMaskModel(saleSession);
        view.loadUserInfo(saleSession.getCustomer());
        view.fillWithoutBarcode(model.getAllItemsWithoutBarcode());
    }

    private ShoppingItem shoppingCartContains(ShoppingItem pattern){
        for (ShoppingItem item : model.getShoppingCart()) {
            if(item.equals(pattern))return item;
        }
        return null;
    }

    void addToShoppingCart(ShoppingItem i){
        if(i.getItemAmount()==0||i.getAmount()==0)return;
        i.setDiscount(view.getDiscount());
        if(!view.isDiscountLocked())view.setDefaultDiscount();
        i.setRawPrice((int) (i.getRawPrice()*(1-(i.getDiscount()/100f))));
        if(i.getRawPrice() > 2000 && !view.isPriceCorrect(i.getRawPrice())) return;
        ShoppingItem inside = shoppingCartContains(i);
        if(inside!=null){
            inside.setRawPrice(i.getRawPrice()+inside.getRawPrice());
            inside.setItemAmount(i.getItemAmount()+inside.getItemAmount());
            view.shoppingCartDataChanged();
        }else {
            model.getShoppingCart().add(i);
            view.setShoppingCartItems(model.getShoppingCart());
        }
        repaintValues();
        view.resetInput();
    }

    void addToShoppingCart(RawPrice rawPrice){
        switch (rawPrice){
            case BACKER:
                addToShoppingCart(ShoppingItem.getOrganic(view.getBackerPrice()));
                return;
            case DEPOSIT:
                int deposit = view.getDepositPrice();
                addToShoppingCart(ShoppingItem.getDeposit(view.isPositiveDeposit()?-deposit:deposit));
                return;
            case ORGANIC:
                addToShoppingCart(ShoppingItem.getBakeryProduct(view.getOrganicPrice()));
        }
        view.resetInput();
    }

    void addToShoppingCart(){
        Item search = model.searchItem(view.getInputItemNumber());
        if(search==null)return;
        ShoppingItem item = new ShoppingItem(search);
        item.setItemAmount(view.getInputItemAmount());
        item.setRawPrice(search.calculatePrice()*item.getItemAmount());
        //TODO if(view.isContainerDiscount())search.setSurcharge(search.getSurcharge().getSurcharge()/2);
        addToShoppingCart(item);
    }

    void addHiddenItemToShoppingCart(){
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setItemAmount(1);
        shoppingItem.setName(view.getHiddenItemName());
        shoppingItem.setVatLow(view.isHiddenItemVATLow());
        shoppingItem.setAmount(view.getInputItemAmount());
        shoppingItem.setRawPrice((int)((view.getHiddenItemPrice()+view.getHiddenItemDeposit())*(1+(shoppingItem.isVatLow()? VAT.LOW.getValue()/100f:VAT.HIGH.getValue()/100f))));
    }

    void clearShoppingCart(){
        model.getShoppingCart().clear();
        view.setShoppingCartItems(model.getShoppingCart());
    }

    void loadSelectedItem(){
        Item i = model.searchItem(view.getInputItemNumber());
        view.loadItemStats(i);
    }


    void removeSelected() {
        model.getShoppingCart().remove(shoppingCartContains(view.getSelected()));
        view.setShoppingCartItems(model.getShoppingCart());
    }

    void repaintValues(){
        view.repaintValues(model.calculateTotalPrice(),model.getValue());
    }

    void searchItems(){
        view.fillSearchSolutions(
                model.searchItems(
                        view.getSearch(),
                        view.isSearchInName(),
                        view.isSearchInPriceList(),
                        view.isSearchInKBNumber(),
                        view.isSearchInBarcode()
                )
        );
    }

    boolean editBarcode(){
        Item item = view.getSelected().extractItem();
        return item != null && model.editBarcode(item.getIid(), view.getBarcode());
    }

    @Override
    public ShoppingMaskView getView() {
        return view;
    }

    @Override
    public ShoppingMaskModel getModel() {
        return model;
    }

    void startPay() {
        new PayView(view.getWindow(),model.getSaleSession(),model.getShoppingCart(),() -> {});
    }
}
