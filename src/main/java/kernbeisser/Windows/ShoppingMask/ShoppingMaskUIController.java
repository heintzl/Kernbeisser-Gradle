package kernbeisser.Windows.ShoppingMask;

import java.awt.*;
import java.util.Objects;
import javax.swing.*;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.ArticleType;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIController
    implements IController<ShoppingMaskUIView, ShoppingMaskModel> {
  private ShoppingMaskUIView view;
  private final ShoppingMaskModel model;
  @Linked private final ShoppingCartController shoppingCartController;

  public ShoppingMaskUIController(SaleSession saleSession) {
    model = new ShoppingMaskModel(saleSession);
    this.shoppingCartController =
        new ShoppingCartController(
            model.getValue(),
            model.getSaleSession().getCustomer().getUserGroup().getSolidaritySurcharge(),
            true);
  }

  private double getRelevantPrice() {
    return view.isPreordered() ? view.getNetPrice() : view.getPrice();
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
        (view.getArticleType() == ArticleType.ARTICLE_NUMBER
            || view.getArticleType() == ArticleType.CUSTOM_PRODUCT);
    try {
      int discount = view.getDiscount();
      if (discount < 0 || discount > 100) {
        view.messageInvalidDiscount();
        return false;
      }
      ShoppingItem item = extractShoppingItemFromUI();
      if (item == null
          || !(item.getItemNetPrice() > 0 && item.getVat() > 0 && item.getItemMultiplier() != 0))
        return false;

      if (piece) {
        if (view.isPreordered() && view.getNetPrice() > 0) {
          item.setItemNetPrice(
              view.getNetPrice() / (item.isWeighAble() ? 1 : item.getContainerSize()));
          item.setItemRetailPrice(item.calculateItemRetailPrice(item.getItemNetPrice()));
        }

        double itemMultiplier =
            view.getAmount()
                * (item.isContainerDiscount() && !item.isWeighAble()
                    ? item.getContainerSize()
                    : 1.0);
        if (itemMultiplier % 1 != 0) {
          if (view.confirmRoundedMultiplier((int) Math.round(itemMultiplier))
              != JOptionPane.YES_OPTION) ;
        }
        item.setItemMultiplier((int) Math.round(itemMultiplier));
      }

      if (item.getItemMultiplier() != 0
          && (view.getArticleType() == ArticleType.RETURN_DEPOSIT || checkStorno(item, piece))) {

        shoppingCartController.addShoppingItem(item);
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

  ShoppingItem extractShoppingItemFromUI() throws UndefinedInputException {
    switch (view.getArticleType()) {
      case ARTICLE_NUMBER:
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

      case BAKED_GOODS:
        return ShoppingItem.createBakeryProduct(getRelevantPrice(), view.isPreordered());

      case PRODUCE:
        return ShoppingItem.createProduce(getRelevantPrice(), view.isPreordered());

      case CUSTOM_PRODUCT:
        ArticleBase customArticle = new Article();

        customArticle.setName(view.getItemName());
        customArticle.setSupplier(view.getSupplier());
        customArticle.setVat(view.getVat());
        customArticle.setNetPrice(
            view.isPreordered()
                ? view.getNetPrice()
                : view.getPrice()
                    / (1. + view.getVat().getValue())
                    / (1. + customArticle.calculateSurcharge()));
        customArticle.setMetricUnits(MetricUnits.PIECE);
        customArticle.setContainerSize(1.);

        ShoppingItem customItem = new ShoppingItem(customArticle, 0, view.isPreordered());
        return customItem;

      case DEPOSIT:
        if (view.getDeposit() < 0) {
          view.messageDepositStorno();
          return null;
        } else {
          return ShoppingItem.createDeposit(view.getDeposit());
        }

      case RETURN_DEPOSIT:
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
      Window window =
          new PayController(
                  model.getSaleSession(),
                  shoppingCartController.getItems(),
                  () -> {
                    view.rememberLogging(
                        model.getSaleSession().getCustomer().getFirstName(),
                        model.getSaleSession().getCustomer().getSurname(),
                        shoppingCartController.getModel().getTotalSum());
                    shoppingCartController.getItems().clear();
                    getView().back();
                    LogInModel.refreshLogInData();
                  },
                  new Dimension(view.getShoppingListSize().width, view.getContent().getHeight()))
              .openAsWindow(view.getWindow(), SubWindow::new);
      view.setCheckoutEnable(false);
      window.addCloseEventListener(e -> view.setCheckoutEnable(true));
    } else {
      view.messageCartIsEmpty();
    }
  }

  @Override
  public boolean commitClose() {
    return shoppingCartController.getItems().size() == 0 || view.confirmClose();
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ShoppingMaskUIController that = (ShoppingMaskUIController) o;
    return Objects.equals(
        model.getSaleSession().getCustomer(), that.model.getSaleSession().getCustomer());
  }

  @Override
  public int hashCode() {
    return Objects.hash(view, model, shoppingCartController);
  }
}
