package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.VAT;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.Window;

public class ShoppingMaskUIController implements Controller {
    private ShoppingMaskUIView view;
    private ShoppingMaskModel model;
    private ShoppingCartController shoppingCartController;

    public ShoppingMaskUIController(Window current, SaleSession saleSession) {
        model = new ShoppingMaskModel(saleSession);
        this.shoppingCartController = new ShoppingCartController(model.getValue(), model.getSaleSession()
                                                                                        .getCustomer()
                                                                                        .getSolidaritySurcharge());
        this.view = new ShoppingMaskUIView(current, this, shoppingCartController);
        view.loadUserInfo(saleSession.getCustomer());
        view.maximize();
        //view.fillWithoutBarcode(model.getAllItemsWithoutBarcode());
    }

    void addToShoppingCart() {
        ShoppingItem item = extract();
        if (item != null) {
            shoppingCartController.addShoppingItem(item);
        }
    }

    void searchByKbNumber() {
        view.defaultSettings();
        Item found = model.getByKbNumber(view.getArticleNumber());
        if (found != null) {
            view.loadItemStats(found);
        }
    }

    void searchBySupplierItemsNumber() {
        view.defaultSettings();
        Item found = model.getBySupplierItemNumber(view.getSuppliersNumber());
        if (found != null) {
            view.loadItemStats(found);
        }
    }

    int getPrice(Item item) {
        return PriceCalculator.getItemPrice(item, 0, model.getSaleSession().getCustomer().getSolidaritySurcharge());
    }

    private ShoppingItem extract() {
        int nettoizedPrice = PriceCalculator.getNetFromGross(view.getPrice(),view.isVatLow());
        switch (view.getOption()) {
            case ShoppingMaskUIView.ARTICLE_NUMBER:
                Item i = null;
                int kb = view.getArticleNumber();
                if (kb != 0) {
                    i = model.getByKbNumber(kb);
                }
                if (i == null) {
                    int supplier = view.getSuppliersNumber();
                    if (supplier != 0) {
                        i = model.getBySupplierItemNumber(supplier);
                    }
                    if (i == null) {
                        view.noArticleFound();
                        return null;
                    }
                }
                ShoppingItem out = new ShoppingItem(i);
                out.setDiscount(view.getDiscount());
                view.setDiscount();
                if (out.isWeighAble()) {
                    out.setAmount(i.getUnit().toUnit(view.getAmount()));
                    out.setItemAmount(1);
                } else {
                    out.setItemAmount((int) view.getAmount());
                }
                return out;
            case ShoppingMaskUIView.BAKED_GOODS:
                return ShoppingItem.getBakeryProduct(nettoizedPrice);
            case ShoppingMaskUIView.DEPOSIT:
                return ShoppingItem.getDeposit(view.getDeposit());
            case ShoppingMaskUIView.CUSTOM_PRODUCT:
                ShoppingItem o = new ShoppingItem();
                o.setItemAmount((int) view.getAmount());
                o.setItemNetPrice(nettoizedPrice);
                o.setName(view.getItemName());
                o.setAmount(o.getUnit().toUnit(view.getAmount()));
                o.setVatLow(view.isVatLow());
                return o;
            case ShoppingMaskUIView.PRODUCE:
                return ShoppingItem.getOrganic(nettoizedPrice);
            case ShoppingMaskUIView.RETURN_DEPOSIT:
                return ShoppingItem.getDeposit(-view.getDeposit());
            default:
                return null;
        }
    }

    /*
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
*/
    @Override
    public ShoppingMaskUIView getView() {
        return view;
    }

    @Override
    public ShoppingMaskModel getModel() {
        return model;
    }

    void startPay() {
        new PayController(view, model.getSaleSession(), model.getShoppingCart(), () -> {
        });
    }

    public void editUserAction() {
        new EditUserController(view, model.getSaleSession().getCustomer(), Mode.EDIT);
    }
}
