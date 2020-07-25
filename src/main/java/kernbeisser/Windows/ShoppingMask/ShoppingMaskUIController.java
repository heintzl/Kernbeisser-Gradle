package kernbeisser.Windows.ShoppingMask;

import java.awt.*;
import javax.swing.*;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIController implements Controller<ShoppingMaskUIView, ShoppingMaskModel> {
  private final ShoppingMaskUIView view;
  private final ShoppingMaskModel model;
  private final ShoppingCartController shoppingCartController;

  public ShoppingMaskUIController(SaleSession saleSession) {
    model = new ShoppingMaskModel(saleSession);
    this.shoppingCartController =
        new ShoppingCartController(
            model.getValue(), model.getSaleSession().getCustomer().getSolidaritySurcharge(), true);
    shoppingCartController.initView();
    this.view = new ShoppingMaskUIView(this, shoppingCartController);
  }

  private boolean checkStorno(ShoppingItem item, boolean piece) {
    boolean result = true;
    boolean exit = true;
    String response = "";
    if (piece && item.getItemMultiplier() < 0) {
      response = view.inputStornoRetailPrice(item.getItemRetailPrice(), false);
      do {
        if (response == null || response.hashCode() == 0 || response.hashCode() == 48) {
          exit = true;
          result = false;
        } else {
          try {
            double alteredRetailPrice = Double.parseDouble(response.replace(',', '.'));
            if (alteredRetailPrice > 0) {
              if (alteredRetailPrice != item.getItemRetailPrice()) {
                item.setItemRetailPrice(alteredRetailPrice);
              }
              item.setName("St. " + item.getName());
              exit = true;
            } else {
              throw (new NumberFormatException());
            }
          } catch (NumberFormatException exception) {
            response = view.inputStornoRetailPrice(item.getItemRetailPrice(), true);
          }
        }
      } while (!exit);
    } else if (!piece && item.getRetailPrice() < 0) {
      result = (view.confirmStorno() == JOptionPane.YES_OPTION);
    }
    return result;
  }

  void emptyShoppingCart() {
    shoppingCartController.emptyCart();
  }

  boolean addToShoppingCart() {
    boolean piece =
        (view.getOption() == ShoppingMaskUIView.ARTICLE_NUMBER
            || view.getOption() == ShoppingMaskUIView.CUSTOM_PRODUCT);
    try {
      int discount = view.getDiscount();
      if (discount < 0 || discount > 100) {
        view.messageInvalidDiscount();
        return false;
      }
      ShoppingItem item = extractShoppingItemFromUI();
      if (!(item.getItemNetPrice() > 0 && item.getVat() > 0 && item.getItemMultiplier() > 0)) return false;
      if (view.isPreordered() && view.getNetPrice() > 0) {
        item.setItemNetPrice(view.getNetPrice() / (item.isWeighAble()?1: item.getContainerSize()));
        item.setItemRetailPrice(item.calculateItemRetailPrice(item.getItemNetPrice()));
      }
      if (piece) {
        double itemMultiplier =
            view.getAmount() * (item.isContainerDiscount() && !item.isWeighAble() ? item.getContainerSize() : 1.0);
        if (itemMultiplier % 1 != 0) {
          if (view.confirmRoundedMultiplier((int) Math.round(itemMultiplier))
              != JOptionPane.YES_OPTION) ;
        }
        item.setItemMultiplier((int) Math.round(itemMultiplier));
      }
      if (item.getItemMultiplier() != 0
          && (view.getOption() == ShoppingMaskUIView.RETURN_DEPOSIT || checkStorno(item, piece))) {
        shoppingCartController.addShoppingItem(item, piece);
        view.setDiscount();
        return true;
      }
      return false;
    } catch (UndefinedInputException undefinedInputException) {
      view.messageNoArticleFound();
      return false;
    }
  }

  void searchByKbNumber() {
    int kbNumber = view.getKBArticleNumber();
    if (kbNumber > 0) {
      view.defaultSettings();
      ShoppingItem found =
          model.getByKbNumber(view.getKBArticleNumber(), view.getDiscount(), view.isPreordered());
      if (found != null) {
        view.loadItemStats(found);
      } else {
        view.setSuppliersItemNumber("");
      }
    }
  }

  void searchBySupplierItemsNumber() {
    view.defaultSettings();
    ShoppingItem found =
        model.getBySupplierItemNumber(
            view.getSuppliersNumber(), view.getDiscount(), view.isPreordered());
    if (found != null) {
      view.loadItemStats(found);
    } else {
      view.setKbNumber("");
    }
  }

  void searchByBarcode(long barcode) {
    view.setOptArticleNo();
    ShoppingItem found = model.getByBarcode(barcode, view.getDiscount(), view.isPreordered());
    if (found != null) {
      view.loadItemStats(found);
      if (!view.isPreordered()) {
        view.addToCart();
      }
    } else {
      view.messageBarcodeNotFound(barcode);
      view.setKbNumber("");
    }
  }

  double calculatePrice(Article article) {
    ShoppingItem shoppingItem = new ShoppingItem(article, 0, view.isPreordered());
    return shoppingItem.getItemRetailPrice()
        * (view.isPreordered() ? article.getContainerSize() : 1);
  }

  double calculateNetPrice(Article article) {
    ShoppingItem shoppingItem = new ShoppingItem(article, 0, view.isPreordered());
    return shoppingItem.getItemNetPrice() * (view.isPreordered() ? article.getContainerSize() : 1);
  }

  public double recalculatePrice(double newNetPrice) throws UndefinedInputException {
    ShoppingItem item = extractShoppingItemFromUI();
    return newNetPrice / item.getItemNetPrice() * item.getItemRetailPrice();
  }

  double getPrice(Article article) {
    ShoppingItem shoppingItem = new ShoppingItem(article, 0, false);
    return shoppingItem.getItemRetailPrice();
  }

  ShoppingItem createCustomItem(Supplier supplier) {
    ArticleBase articleBase = new ArticleBase();
    articleBase.setSupplier(supplier);
    articleBase.setVat(view.getVat());
    return new ShoppingItem(articleBase, view.getDiscount(), view.isPreordered());
  }

  private ShoppingItem extractShoppingItemFromUI() throws UndefinedInputException {
    ShoppingItem shoppingItem = view.getCurrentItem();
    switch (view.getOption()) {
      case ShoppingMaskUIView.ARTICLE_NUMBER:
        if (shoppingItem != null) return shoppingItem;
        int discount = view.getDiscount();
        boolean preordered = view.isPreordered();
        int kbArticleNumber = view.getKBArticleNumber();
        if (kbArticleNumber != 0) {
          return model.getByKbNumber(kbArticleNumber, discount, preordered);
        }
        int suppliersNumber = view.getSuppliersNumber();
        if (suppliersNumber != 0) {
          return model.getBySupplierItemNumber(suppliersNumber, discount, preordered);
        }
        throw new UndefinedInputException();

      case ShoppingMaskUIView.BAKED_GOODS:
        return ShoppingItem.createBakeryProduct(view.getPriceVATIncluded(), view.isPreordered());

      case ShoppingMaskUIView.PRODUCE:
        return ShoppingItem.createOrganic(view.getPriceVATIncluded(), view.isPreordered());

      case ShoppingMaskUIView.CUSTOM_PRODUCT:
        ArticleBase customArticle = new Article();

        customArticle.setName(view.getItemName());
        customArticle.setSupplier(view.getSupplier());
        customArticle.setVat(view.getVat());
        customArticle.setNetPrice(
            view.isPreordered()
                ? view.getNetPrice()
                : view.getPriceVATIncluded()
                    / (1. + view.getVat().getValue())
                    / (1. + customArticle.calculateSurcharge()));
        customArticle.setMetricUnits(MetricUnits.PIECE);
        customArticle.setContainerSize(1.);

        ShoppingItem customItem = new ShoppingItem(customArticle, 0, view.isPreordered());
        return customItem;

      case ShoppingMaskUIView.DEPOSIT:
        if (view.getDeposit() < 0) {
          view.messageDepositStorno();
          return null;
        } else {
          return ShoppingItem.createDeposit(view.getDeposit());
        }

      case ShoppingMaskUIView.RETURN_DEPOSIT:
        if (view.getDeposit() < 0) {
          view.messageDepositStorno();
          return null;
        } else {
          return ShoppingItem.createDeposit(view.getDeposit() * (-1));
        }
      default:
        return null;
    }
  }

  @Override
  public @NotNull ShoppingMaskUIView getView() {
    return view;
  }

  @Override
  public @NotNull ShoppingMaskModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.loadUserInfo(model.getSaleSession());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  void startPay() {
    if (shoppingCartController.getItems().size() > 0) {
      new PayController(
              model.getSaleSession(),
              shoppingCartController.getItems(),
              () -> {
                getView().back();
              },
              new Dimension(view.getShoppingListSize().width, view.getContent().getHeight()))
          .openAsWindow(view.getWindow(), SubWindow::new);
    } else {
      view.messageCartIsEmpty();
    }
  }

  void openSearchWindow() {
    new ArticleSelectorController(this::searchWindowResult)
        .openAsWindow(view.getWindow(), SubWindow::new);
  }

  void searchWindowResult(Article article) {
    view.setOptArticleNo();
    view.loadItemStats(new ShoppingItem(article, view.getDiscount(), view.isPreordered()));
  }

  void editUserAction() {
    new EditUserController(model.getSaleSession().getCustomer(), Mode.EDIT)
        .openAsWindow(view.getWindow(), SubWindow::new);
  }

  public void processBarcode(String barcode) {
    try {
      long bc = Long.parseLong(barcode);
      searchByBarcode(bc);
    } catch (NumberFormatException e) {
      view.messageInvalidBarcode(barcode);
    }
  }
}
