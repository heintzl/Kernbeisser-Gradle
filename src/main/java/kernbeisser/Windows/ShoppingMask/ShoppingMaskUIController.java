package kernbeisser.Windows.ShoppingMask;

import java.awt.event.KeyEvent;
import java.util.Objects;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.*;
import kernbeisser.Dialogs.RememberDialog;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Enums.ArticleType;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIController extends Controller<ShoppingMaskUIView, ShoppingMaskModel> {
  @Linked private final ShoppingCartController shoppingCartController;

  private BarcodeCapture barcodeCapture;
  private KeyCapture keyCapture;

  private final ShoppingMaskUIView view;

  public ShoppingMaskUIController(SaleSession saleSession) throws NotEnoughCreditException {
    super(new ShoppingMaskModel(saleSession));
    this.shoppingCartController =
        new ShoppingCartController(
            model.getValue(),
            model.getSaleSession().getCustomer().getUserGroup().getSolidaritySurcharge(),
            true);
    this.view = getView();
  }

  private double getRelevantPrice() {
    return view.isPreordered() ? view.getNetPrice() : view.getRetailPrice();
  }

  private boolean checkStorno(ShoppingItem item, boolean piece) {
    boolean result = true;
    boolean exit = true;
    String response;
    if (piece && item.getItemMultiplier() < 0) {
      response = view.inputStornoRetailPrice(item.getItemRetailPrice(), false);
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
            response = view.inputStornoRetailPrice(item.getItemRetailPrice(), true);
          }
        }
      } while (!exit);
    } else if (!piece && item.getRetailPrice() < 0) {
      item.setName("St. " + item.getName());
      result = (view.confirmStorno());
    }
    return result;
  }

  void emptyShoppingCart() {
    if (shoppingCartController.getItems().size() != 0 && view.confirmEmptyCart())
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
          || !(item.getItemNetPrice() > 0
              && item.getVatValue() > 0
              && item.getItemMultiplier() != 0)) return false;

      if (piece) {
        if (view.isPreordered() && view.getNetPrice() > 0) {
          item.setItemNetPrice(
              view.getNetPrice() / (item.isWeighAble() ? 1 : item.getContainerSize()));
          item.setItemRetailPriceFromNetPrice();
        }

        double itemMultiplier =
            view.getItemMultiplier()
                * (item.isContainerDiscount() && !item.isWeighAble()
                    ? item.getContainerSize()
                    : 1.0);
        item.setItemMultiplier((int) Math.round(itemMultiplier));
        if (itemMultiplier % 1 != 0) {
          view.messageRoundedMultiplier(item.getDisplayAmount());
        }
      }

      if (item.getItemMultiplier() != 0
          && (view.getArticleType() == ArticleType.RETURN_DEPOSIT || checkStorno(item, piece))
          && checkWarningThresholds(item)) {
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

  private boolean checkWarningThresholds(ShoppingItem item) {
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
    int kbNumber = view.getKBArticleNumber();
    if (kbNumber > 0) {
      view.defaultSettings();
      Article found = model.getArticleByKbNumber(kbNumber);
      if (found != null) {
        view.loadArticleStats(found);
      } else {
        view.setSuppliersItemNumber("");
      }
    }
  }

  void searchBySupplierItemsNumber() {
    Supplier supplier = view.getSupplier();
    if (supplier == null) view.messageNoSupplier();
    view.defaultSettings();
    Article found = model.getArticleBySupplierItemNumber(supplier, view.getSuppliersNumber());
    if (found != null) {
      view.loadArticleStats(found);
    } else {
      view.setKbNumber("");
    }
  }

  void searchByBarcode(long barcode) {
    view.setOptArticleNo();
    try {
      Article found = model.getByBarcode(barcode);
      view.loadArticleStats(found);
      if (!view.isPreordered()) {
        view.addToCart();
      }
    } catch (NoResultException e) {
      Tools.noArticleFoundForBarcodeWarning(view.getContent(), Long.toString(barcode));
      view.setKbNumber("");
    }
  }

  public Double recalculatePrice(
      Article article, double discount, boolean preordered, boolean overwriteNetPrice)
      throws NullPointerException {
    if (overwriteNetPrice) {
      article.setNetPrice(view.getNetPrice());
    }
    return Articles.calculateArticleRetailPrice(article, discount, preordered)
        * (preordered && !article.isWeighable() && !overwriteNetPrice
            ? article.getContainerSize()
            : 1.0);
  }

  public Article extractArticleFromUI() throws NullPointerException {
    Article article = null;
    int kbNumber = view.getKBArticleNumber();
    int suppliersItemNumber = view.getSuppliersNumber();
    Supplier supplier = view.getSupplier();
    double netPrice = view.getNetPrice();
    VAT vat = view.getVat();
    if (kbNumber > 0 && view.getArticleType() == ArticleType.ARTICLE_NUMBER) {
      article =
          Articles.getByKbNumber(kbNumber, false).orElse(ObjectState.wrap(null, 0)).getValue();
    } else if (supplier != null
        && suppliersItemNumber > 0
        && view.getArticleType() == ArticleType.ARTICLE_NUMBER) {
      article = Articles.getBySuppliersItemNumber(supplier, suppliersItemNumber).orElse(null);
    }
    if (article == null) {
      article = new Article();
      article.setNetPrice(netPrice);
      article.setVat(vat);
      article.setSupplier(supplier);
      article.setSurchargeGroup(
          Objects.requireNonNull(supplier).getOrPersistDefaultSurchargeGroup());
    }
    return article;
  }

  Article createCustomArticle(Supplier supplier) {
    Article article = new Article();
    article.setSupplier(supplier);
    article.setVat(view.getVat());
    if (supplier != null) {
      article.setSurchargeGroup(supplier.getOrPersistDefaultSurchargeGroup());
    }
    article.setName("");
    return article;
  }

  ShoppingItem extractShoppingItemFromUI() throws UndefinedInputException {
    synchronized (Access.ACCESS_LOCK) {
      AccessManager defaultManager = Access.getDefaultManager();
      try {
        Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
        switch (view.getArticleType()) {
          case ARTICLE_NUMBER:
            int discount = view.getDiscount();
            boolean preordered = view.isPreordered();
            int kbArticleNumber = view.getKBArticleNumber();
            if (kbArticleNumber != 0) {
              ShoppingItem item = model.getItemByKbNumber(kbArticleNumber, discount, preordered);
              if (item != null) {
                return item;
              }
            }
            int suppliersNumber = view.getSuppliersNumber();
            if (suppliersNumber != 0 && view.getSupplier() != null) {
              return model.getItemBySupplierItemNumber(
                  view.getSupplier(), suppliersNumber, discount, preordered);
            }
            throw new UndefinedInputException();

          case BAKED_GOODS:
            return ShoppingItem.createBakeryProduct(getRelevantPrice(), view.isPreordered());

          case PRODUCE:
            return ShoppingItem.createProduce(getRelevantPrice(), view.isPreordered());

          case CUSTOM_PRODUCT:
            Article customArticle =
                Articles.getCustomArticleVersion(
                    before -> {
                      before.setName(view.getArticleName());
                      before.setSupplier(view.getSupplier());
                      before.setVat(view.getVat());
                      if (view.isPreordered()) {
                        before.setSurchargeGroup(
                            view.getSupplier().getOrPersistDefaultSurchargeGroup());
                      } else {
                        before.setSurchargeGroup(
                            Supplier.getKKSupplier().getOrPersistDefaultSurchargeGroup());
                      }

                      before.setNetPrice(
                          view.isPreordered()
                              ? view.getNetPrice()
                              : view.getRetailPrice()
                                  / (1. + view.getVat().getValue())
                                  / (1. + before.getSurchargeGroup().getSurcharge()));
                      before.setMetricUnits(MetricUnits.PIECE);
                      before.setContainerSize(1.);
                    });

            return new ShoppingItem(customArticle, 0, view.isPreordered());

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
      } finally {
        Access.setDefaultManager(defaultManager);
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
    keyCapture.addF2ToF8NumberActions(view::setItemMultiplier);
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
                view.back();
              })
          .openIn(new SubWindow(view.traceViewContainer()));
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
        .openIn(new SubWindow(view.traceViewContainer()));
  }

  void searchWindowResult(Article article) {
    view.setOptArticleNo();
    view.loadArticleStats(article);
    view.setFocusOnAmount();
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
    return Objects.hash(view, model, shoppingCartController);
  }

  public void openUserInfo() {
    new UserInfoController(model.getSaleSession().getCustomer())
        .openIn(new SubWindow(view.traceViewContainer()));
  }
}
