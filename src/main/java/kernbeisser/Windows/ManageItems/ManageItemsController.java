package kernbeisser.Windows.ManageItems;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.ItemFilter.ItemFilterView;

class ManageItemsController implements Controller {

    private ManageItemsView view;
    private ManageItemsModel model;
    ManageItemsController(ManageItemsView view){
        this.view=view;
        this.model=new ManageItemsModel();
        view.fillSupplier(model.getAllSupplier());
        refreshPriceLists();
        view.setSelectedPriceList(PriceList.getSingleItemPriceList());
        view.setFilters();
    }

    void requestFilter(){
        new ItemFilterView(view){
            @Override
            public void finish() {
                setFilter(getSelectedPriceList(),getSelectedSupplier());
            }
        };
    }

    void refreshPriceLists(){
        view.setPriceLists(model.getAllPriceListNames());
    }

    void setFilter(PriceList p,Supplier s){
        model.setItemFilterPriceList(p);
        model.setItemFilterSupplier(s);
    }

    void loadSearchSolutions(){
        String search = view.getSearchBar();
        boolean searchInKK = view.isSearchInCatalog();
        int selected = view.getSelectedTab();
        view.setSearchSolution(model.searchItems(search,search,search),searchInKK ? model.searchItemKKs(search,search,search) : null);
        view.setSelectedTab(selected == 1 && searchInKK ? 1 : 0);
    }


    @Override
    public void refresh() {

    }

    @Override
    public ManageItemsView getView() {
        return view;
    }

    @Override
    public ManageItemsModel getModel() {
        return model;
    }

    boolean save() {
        return model.save(view.collect());
    }

    void pasteKbItem() {
        view.pasteData(view.getSelectedKbItem());
    }

    void pasteKkItem() {
        view.pasteData(view.getSelectedKkItem());
    }
}
