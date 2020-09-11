package kernbeisser.CustomComponents;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTree.ChildFactory;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;

/**
 * A JTree filled with all SuperGroups and Groups from the PriceLists include the PriceLists from
 * the Database
 */
public class PriceListTree extends ObjectTree<PriceList> {
  public PriceListTree() {
    this(true);
  }

  /**
   * fills the JTree with the SuperGroups than the UnderGroups from the SuperGroups the Groups and
   * at least the PriceLists as UnderGroup from theGroups
   *
   * @param optionToEdit if true there is a Option to get to the Add PriceLists window to add the
   *     needed PriceList
   */
  public PriceListTree(boolean optionToEdit) {
    super(
        new ChildFactory<PriceList>() {
          @Override
          public Collection<PriceList> produce(PriceList priceList) {
            return priceList.getAllPriceLists();
          }

          @Override
          public String getName(PriceList priceList) {
            return priceList.getName();
          }
        },
        "Preislisten",
        PriceList.getAllHeadPriceLists());
    if (optionToEdit) {
      PriceList p =
          new PriceList() {
            @Override
            public int getId() {
              return Integer.MIN_VALUE;
            }
          };
      p.setName("Neu Hinzuf\u00fcgen/Bearbeiten");
      getStartValues().add(p);
      refresh();
      addSelectionListener(
          e -> {
            if (e.getId() == Integer.MIN_VALUE) {
              new ManagePriceListsController() {
                @Override
                public void finish() {
                  PriceListTree.this.setModel(new PriceListTree().getModel());
                }
              };
            }
          });
    }
  }
}
