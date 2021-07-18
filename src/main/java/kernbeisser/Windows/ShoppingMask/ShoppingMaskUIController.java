package kernbeisser.Windows.ShoppingMask;

import java.awt.event.KeyEvent;
import java.util.Objects;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.*;
import kernbeisser.Dialogs.RememberDialog;
import kernbeisser.Enums.ArticleType;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIController extends Controller<ShoppingMaskUIView, ShoppingMaskModel> {
  @Linked private final ShoppingCartController shoppingCartController;

  private BarcodeCapture barcodeCapture;
  private KeyCapture keyCapture;

  public ShoppingMaskUIController(SaleSession saleSession) throws NotEnoughCreditException {
    super(new ShoppingMaskModel(saleSession));
    this.shoppingCartController =
        new ShoppingCartController(
            model.getValue(),
            model.getSaleSession().getCustomer().getUserGroup().getSolidaritySurcharge(),
            true);
  }

  private double getRelevantPrice() {
    var view = getView();
    return view.isPreordered() ? view.getNetPrice() : getView().getPrice();
  }

  private boolean checkStorno(ShoppingItem item, boolean piece) {
    boolean result = true;
    boolean exit = true;
    String response;
    if (piece && item.getItemMultiplier() < 0) {
      response = getView().inputStornoRetailPrice(item.getItemRetailPrice(), false);
      do {
        if (response == null || response.hashCode() == 0 || response.hashCode() == 48) {
          result = false;
        } else {
          try {
            double alteredRetailPrice = Double.parseDouble(response.replace(',', '.'));
            if (alteredRetailPrice > 0) {
              if (alteredRetailPrice != item.getItemRetailPrice()) {
                item.setItemRetailPrice(alteredRetailPrice);
              }
              exit = true;
            } else {
              throw (new NumberFormatException());
            }
          } catch (NumberFormatException exception) {
            exit = false;
            response = getView().inputStornoRetailPrice(item.getItemRetailPrice(), true);
          }
        }
      } while (!exit);
    } else if (!piece && item.getRetailPrice() < 0) {
      item.setName("St. " + item.getName());
      result = (getView().confirmStorno());
    }
    return result;
  }

  void emptyShoppingCart() {
    if (shoppingCartController.getItems().size() != 0 && getView().confirmEmptyCart())
      shoppingCartController.emptyCart();
  }

  boolean addToShoppingCart() {
    boolean piece =
        (getView().getArticleType() == ArticleType.ARTICLE_NUMBER
            || getView().getArticleType() == ArticleType.CUSTOM_PRODUCT);
    try {
      int discount = getView().getDiscount();
      if (discount < 0 || discount > 100) {
        getView().messageInvalidDiscount();
        return false;
      }
      ShoppingItem item = extractShoppingItemFromUI();
      if (item == null
          || !(item.getItemNetPrice() > 0
              && item.getVatValue() > 0
              && item.getItemMultiplier() != 0)) return false;

      if (piece) {
        if (getView().isPreordered() && getView().getNetPrice() > 0) {
          item.setItemNetPrice(
              getView().getNetPrice() / (item.isWeighAble() ? 1 : item.getContainerSize()));
          item.setItemRetailPrice(item.calculateItemRetailPrice(item.getItemNetPrice()));
        }

        double itemMultiplier =
            getView().getAmount()
                * (item.isContainerDiscount() && !item.isWeighAble()
                    ? item.getContainerSize()
                    : 1.0);
        item.setItemMultiplier((int) Math.round(itemMultiplier));
        if (itemMultiplier % 1 != 0) {
          getView().messageRoundedMultiplier(item.getDisplayAmount());
        }
      }

      if (item.getItemMultiplier() != 0
          && (getView().getArticleType() == ArticleType.RETURN_DEPOSIT || checkStorno(item, piece))
          && checkWarningThresholds(item)) {
        shoppingCartController.addShoppingItem(item);
        getView().setDiscount();
        return true;
      }
      return false;
    } catch (UndefinedInputException undefinedInputException) {
      getView().messageNoArticleFound();
      return false;
    }
  }

  private boolean checkWarningThresholds(ShoppingItem item) {
    var view = getView();
    return (shoppingCartController.getRemainingValue() - item.getRetailPrice() >= 0
            || model.getSaleSession().getCustomer().mayGoUnderMin()
            || view.messageUnderMin())
        && (item.getRetailPrice() < Setting.SHOPPING_PRICE_WARNING_THRESHOLD.getDoubleValue()
            || view.confirmPriceWarning())
        && (item.isWeighAble()
            || item.getKbNumber() < 0
            || item.getItemMultiplier() / (item.isContainerDiscount() ? item.getContainerSize() : 1)
                < Setting.SHOPPING_AMOUNT_WARNING_THRESHOLD.getIntValue()
            || view.confirmAmountWarning());
  }

  void searchByKbNumber() {
    int kbNumber = getView().getKBArticleNumber();
    if (kbNumber > 0) {
      getView().defaultSettings();
      ShoppingItem found =
          model.getByKbNumber(
              getView().getKBArticleNumber(), getView().getDiscount(), getView().isPreordered());
      if (found != null) {
        getView().loadItemStats(found);
      } else {
        getView().setSuppliersItemNumber("");
      }
    }
  }

  void searchBySupplierItemsNumber() {
    Supplier supplier = getView().getSupplier();
    if (supplier == null) getView().messageNoSupplier();
    getView().defaultSettings();
    ShoppingItem found =
        model.getBySupplierItemNumber(
            supplier,
            getView().getSuppliersNumber(),
            getView().getDiscount(),
            getView().isPreordered());
    if (found != null) {
      getView().loadItemStats(found);
    } else {
      getView().setKbNumber("");
    }
  }

  void searchByBarcode(long barcode) {
    getView().setOptArticleNo();
    try {
      ShoppingItem found =
          model.getByBarcode(barcode, getView().getDiscount(), getView().isPreordered());
      getView().loadItemStats(found);
      if (!getView().isPreordered()) {
        getView().addToCart();
      }
    } catch (NoResultException e) {
      getView().messageBarcodeNotFound(barcode);
      getView().setKbNumber("");
    }
  }

  public double recalculatePrice(double newNetPrice) {
    try {
      ShoppingItem item = extractShoppingItemFromUI();
      return newNetPrice / item.getItemNetPrice() * item.getItemRetailPrice();
    } catch (UndefinedInputException e) {
      return 0.0;
    }
  }

  ShoppingItem createCustomItem(Supplier supplier) {
    Article articleBase = new Article();
    articleBase.setSupplier(supplier);
    articleBase.setVat(getView().getVat());
    return new ShoppingItem(articleBase, getView().getDiscount(), getView().isPreordered());
  }

  ShoppingItem extractShoppingItemFromUI() throws UndefinedInputException {
    synchronized (Access.ACCESS_LOCK) {
      AccessManager before = Access.getDefaultManager();
      try {
        Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
        switch (getView().getArticleType()) {
          case ARTICLE_NUMBER:
            int discount = getView().getDiscount();
            boolean preordered = getView().isPreordered();
            int kbArticleNumber = getView().getKBArticleNumber();
            if (kbArticleNumber != 0) {
              ShoppingItem item = model.getByKbNumber(kbArticleNumber, discount, preordered);
              if (item != null) {
                return item;
              }
            }
            int suppliersNumber = getView().getSuppliersNumber();
            if (suppliersNumber != 0 && getView().getSupplier() != null) {
              return model.getBySupplierItemNumber(
                  getView().getSupplier(), suppliersNumber, discount, preordered);
            }
            throw new UndefinedInputException();

          case BAKED_GOODS:
            return ShoppingItem.createBakeryProduct(getRelevantPrice(), getView().isPreordered());

          case PRODUCE:
            return ShoppingItem.createProduce(getRelevantPrice(), getView().isPreordered());

          case CUSTOM_PRODUCT:
            Article customArticle = new Article();

            customArticle.setName(getView().getItemName());
            customArticle.setSupplier(getView().getSupplier());
            customArticle.setVat(getView().getVat());
            if (getView().isPreordered()) {
              customArticle.setSurchargeGroup(
                  getView().getSupplier().getOrPersistDefaultSurchargeGroup());
            } else {
              customArticle.setSurchargeGroup(
                  Supplier.getKKSupplier().getOrPersistDefaultSurchargeGroup());
            }

            customArticle.setNetPrice(
                getView().isPreordered()
                    ? getView().getNetPrice()
                    : getView().getPrice()
                        / (1. + getView().getVat().getValue())
                        / (1. + customArticle.getSurchargeGroup().getSurcharge()));
            customArticle.setMetricUnits(MetricUnits.PIECE);
            customArticle.setContainerSize(1.);

            return new ShoppingItem(customArticle, 0, getView().isPreordered());

          case DEPOSIT:
            if (getView().getDeposit() < 0) {
              getView().messageDepositStorno();
              return null;
            } else {
              return ShoppingItem.createDeposit(getView().getDeposit());
            }

          case RETURN_DEPOSIT:
            if (getView().getDeposit() < 0) {
              getView().messageDepositStorno();
              return null;
            } else {
              return ShoppingItem.createDeposit(getView().getDeposit() * (-1));
            }
          default:
            return null;
        }
      } finally {
        Access.setDefaultManager(before);
      }
    }
  }

  @Override
  public @NotNull ShoppingMaskModel getModel() {
    return model;
  }

  void loadShoppingItem(ShoppingItem item) {
    if (item.getKbNumber() > 0)
      searchWindowResult(item.getArticleNow().orElseThrow(RuntimeException::new));
  }

  @Override
  public void fillView(ShoppingMaskUIView shoppingMaskUIView) {
    ShoppingMaskUIView view = getView();
    view.loadUserInfo(model.getSaleSession());
    view.setFocusOnKBNumber();
    shoppingCartController
        .getView()
        .getShoppingItemsTable()
        .addDoubleClickListener(this::loadShoppingItem);
    barcodeCapture = new BarcodeCapture(this::processBarcode);

    keyCapture = new KeyCapture();
    keyCapture.add(KeyEvent.VK_F2, () -> view.setAmount("2"));
    keyCapture.add(KeyEvent.VK_F3, () -> view.setAmount("3"));
    keyCapture.add(KeyEvent.VK_F4, () -> view.setAmount("4"));
    keyCapture.add(KeyEvent.VK_F5, () -> view.setAmount("5"));
    keyCapture.add(KeyEvent.VK_F6, () -> view.setAmount("6"));
    keyCapture.add(KeyEvent.VK_F7, () -> view.setAmount("8"));
    keyCapture.add(KeyEvent.VK_F8, () -> view.setAmount("10"));
    keyCapture.add(KeyEvent.VK_INSERT, () -> view.articleTypeChange(ArticleType.PRODUCE));
    keyCapture.add(KeyEvent.VK_PAGE_UP, () -> view.articleTypeChange(ArticleType.BAKED_GOODS));
    keyCapture.add(KeyEvent.VK_END, () -> view.articleTypeChange(ArticleType.ARTICLE_NUMBER));
    keyCapture.addALT(KeyEvent.VK_S, view::openSearchWindow);
    keyCapture.addCTRL(KeyEvent.VK_F, view::openSearchWindow);
    User customer = model.getSaleSession().getCustomer();
    String infoMessage;
    if (customer.equals(model.getSaleSession().getSeller())) {
      infoMessage =
          "Dein Guthaben beträgt weniger als 0.01€.\n"
              + "Bitte sei Dir bewusst, dass Schulden Zinsen verursachen.";
    } else {
      infoMessage =
          "Das Guthaben von "
              + customer.getFullName()
              + " beträgt weniger als 0.01€.\n"
              + "Bitte seid Euch bewusst, dass Schulden Zinsen verursachen.";
    }
    if (customer.getUserGroup().getValue() <= 0) {
      RememberDialog.showDialog(
          customer, "CustomerDebtWarning", null, infoMessage, "Kein Guthaben vorhanden");
    }
  }

  void startPay() {
    if (shoppingCartController.getItems().size() > 0) {
      new PayController(
              model.getSaleSession(),
              shoppingCartController.getItems(),
              () -> {
                shoppingCartController.getItems().clear();
                getView().back();
                LogInModel.refreshLogInData();
              })
          .openIn(new SubWindow(getView().traceViewContainer()));
    } else {
      getView().messageCartIsEmpty();
    }
  }

  @Override
  public boolean commitClose() {
    return shoppingCartController.getItems().size() == 0 || getView().confirmClose();
  }

  void openSearchWindow() {
    new ArticleSelectorController(this::searchWindowResult)
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  void searchWindowResult(Article article) {
    getView().setOptArticleNo();
    getView()
        .loadItemStats(
            new ShoppingItem(article, getView().getDiscount(), getView().isPreordered()));
    getView().setFocusOnAmount();
  }

  public void processBarcode(String barcode) {
    try {
      long bc = Long.parseLong(barcode);
      searchByBarcode(bc);
    } catch (NumberFormatException e) {
      getView().messageInvalidBarcode(barcode);
    }
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return barcodeCapture.processKeyEvent(e) || keyCapture.processKeyEvent(e);
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
    return Objects.hash(getView(), model, shoppingCartController);
  }

  public void openUserInfo() {
    new UserInfoController(model.getSaleSession().getCustomer())
        .openIn(new SubWindow(getView().traceViewContainer()));
  }
}
