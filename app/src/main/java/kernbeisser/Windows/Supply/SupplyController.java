package kernbeisser.Windows.Supply;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Optional;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Enums.*;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PrintLabels.PrintLabelsController;
import kernbeisser.Windows.PrintLabels.PrintLabelsModel;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
import kernbeisser.Windows.Supply.SupplySelector.ResolveStatus;
import kernbeisser.Windows.Supply.SupplySelector.SupplySelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Log4j2
public class SupplyController extends Controller<SupplyView, SupplyModel> {
  public final KeyCapture keyCapture;
  public final BarcodeCapture barcodeCapture;

  @Key(PermissionKey.ACTION_OPEN_SUPPLY)
  public SupplyController() {
    super(new SupplyModel());
    model.setPrintPoolBefore(ArticlePrintPool.getPrintPoolAsMap());
    keyCapture = new KeyCapture();
    barcodeCapture = new BarcodeCapture(this::processBarcode);
  }

  public void editPrintPool(ShoppingItem item) {
    Integer newValue =
        Tools.integerInputDialog(getView().getContent(), model.getPrintNumber(item), i -> i >= 0);
    if (newValue == null) {
      return;
    }
    model.setPrintNumber(item, newValue);
    getView().refreshRow(item);
  }

  public void editItemMultiplier(ShoppingItem item) {
    Integer newValue =
        Tools.integerInputDialog(
            getView().getContent(), -(int) Math.round(item.getContainerCount()));
    if (newValue == null) {
      return;
    }
    model.setContainerMultiplier(item, newValue);
    model.setPrintNumber(item, model.calculatePrintNumberFromItem(item));
    getView().refreshRow(item);
  }

  private void preparePrint() {
    model.print();
    getView().repaintTable();
  }

  @Override
  public void fillView(SupplyView supplyView) {
    var view = getView();
    view.setSuppliers(model.getAllSuppliers());
    keyCapture.addF2ToF8NumberActions(getView()::setAmount);
  }

  void searchShoppingItem(Supplier supplier, int supNr) {
    if (supNr == 0) {
      getView().noArticleFound();
      return;
    }
    try {
      getView()
          .getObjectForm()
          .setSource(
              model.findBySuppliersItemNumber(supplier, supNr).orElseThrow(NoResultException::new));
      getView().setAddAvailable(true);
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  public void processBarcode(String s) {
    try {
      getView().getObjectForm().setSource(PrintLabelsModel.getByBarcode(s));
      getView().addItem();
    } catch (NoResultException e) {
      Tools.noArticleFoundForBarcodeWarning(getView().getContent(), s);
    }
  }

  private void checkInput() throws NoResultException, CannotParseException {
    if (!model.articleExists(getView().getSelected(), getView().getSuppliersItemNumber())) {
      throw new NoResultException();
    }
  }

  public void addItem(double amount) throws CannotParseException, NoResultException {
    checkInput();
    Article article = getView().getObjectForm().getData(null);
    if (!article.equals(getView().getObjectForm().getOriginal())) {
      if (!getView().confirmChanges()) {
        getView().getObjectForm().setSource(getView().getObjectForm().getOriginal());
        return;
      }
      getView().getObjectForm().setShowSuccessDialog(false);
      getView().getObjectForm().applyMode(Mode.EDIT);
    }
    ShoppingItem item = new ShoppingItem(article, 0, false);
    model.setContainerMultiplier(item, amount);
    model.addShoppingItem(item);
    setPrintNumber(item);
    getView().noArticleFound();
    recalculateTotal();
    getView().setShoppingItems(model.getShoppingItems());
  }

  void commit() {
    model.commit(getView()::messageSupplyExistsInDB);
    if (model.getShoppingItems().size() > 0) {
      model.print();
      new PrintLabelsController().openIn(new SubWindow(getView().traceViewContainer()));
    }
    model.clearShoppingItems();
    recalculateTotal();
    getView().back();
  }

  @Override
  protected boolean commitClose() {
    if (model.isPrintSelected() && getView().shouldPrintLabels()) model.print();
    return model.getShoppingItems().size() == 0 || getView().commitClose();
  }

  public void remove(ShoppingItem selectedObject) {
    model.removeShoppingItem(selectedObject);
    recalculateTotal();
  }

  public void recalculateTotal() {
    double items =
        model.getShoppingItems().stream()
            .mapToDouble(
                e ->
                    Math.abs(
                        e.getItemNetPrice()
                            * (e.isWeighAble()
                                ? (e.getItemMultiplier() / 1000.)
                                : e.getItemMultiplier())))
            .sum();
    double produce = model.getAppendedProducePrice();
    getView().setTotal(produce + items);
    getView().setProduce(produce);
  }

  public void setPrintNumber(ShoppingItem item) {
    model.setPrintNumber(item, model.calculatePrintNumberFromItem(item));
    getView().repaintTable();
  }

  public void increaseItemPrintNumber(ShoppingItem item) {
    model.setPrintNumber(item, model.getPrintNumber(item) + 1);
    getView().refreshRow(item);
  }

  public int getPrintNumber(ShoppingItem item) {
    return model.getPrintNumber(item);
  }

  public void openImportSupplyFile() {
    Dimension viewSize = getView().getSize();
    Dimension size =
        new Dimension(
            (int) viewSize.getWidth(),
            (int) (viewSize.getHeight() * Setting.SUBWINDOW_SIZE_FACTOR.getFloatValue()));
    new SupplySelectorController(
            (supply, shoppingItems) -> {
              for (ShoppingItem item : shoppingItems) {
                model.addShoppingItem(item);
                setPrintNumber(item);
              }
              getView().setShoppingItems(model.getShoppingItems());
              getModel().setSupply(supply);
              getModel()
                  .setAppendedProducePrice(
                      supply.getAllLineContents().stream()
                          .filter(e -> e.getStatus() == ResolveStatus.PRODUCE)
                          .mapToDouble(LineContent::getTotalPrice)
                          .sum());
              recalculateTotal();
            })
        .openIn(new SubWindow(getView().traceViewContainer()).withSize(size));
  }

  public static boolean shouldBecomeShoppingItem(LineContent content) {
    return !(content.getStatus() == ResolveStatus.IGNORE
        || content.getStatus() == ResolveStatus.PRODUCE);
  }

  public static Article findOrCreateArticle(
      Supplier kkSupplier, LineContent content, boolean noBarcode) {
    Optional<Article> articleInDb =
        ArticleRepository.getBySuppliersItemNumber(kkSupplier, content.getKkNumber());
    ShopRange shopRange = (content.getContainerMultiplier() - Tools.ifNull(content.getUserPreorderCount(), 0)
            > 0 ? ShopRange.PERMANENT_RANGE : ShopRange.IN_RANGE);
    if (articleInDb.isPresent()) {
      boolean dirty = false;
      @Cleanup EntityManager em = DBConnection.getEntityManager();
      @Cleanup("commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      Article article = em.find(Article.class, articleInDb.map(Article::getId).get());
      double newPrice = content.getPriceKb();
      boolean newWeighable = content.isWeighableKb();

      String logInfo = "Article [" + article.getSuppliersItemNumber() + "]:";
      ShopRange articleShopRange = article.getShopRange();
      if (!articleShopRange.equals(ShopRange.PERMANENT_RANGE)) {
        if (!articleShopRange.equals(shopRange)) {
          article.setShopRange(shopRange);
          dirty = true;
          logInfo += " updated shop range [" + article.getShopRange() + "] -";
        }
      }
      if (Math.abs(article.getNetPrice() - newPrice) >= 0.01) {
        dirty = true;
        logInfo += " price change [" + article.getNetPrice() + " -> " + newPrice + "] -";
        article.setNetPrice(newPrice);
      }
      if (article.isWeighable() != newWeighable) {
        dirty = true;
        logInfo += " weighable change [" + article.isWeighable() + " -> " + newWeighable + "] -";
        article.setWeighable(newWeighable);
      }
      if (dirty) {
        em.merge(article);
        log.info(logInfo.substring(0, logInfo.length() - 2));
      }
      return article;
    }
    return createArticle(content, noBarcode, shopRange);
  }

  public static ShoppingItem createShoppingItem(
      Supplier kkSupplier, LineContent content, int orderNo, boolean ignoreBarcode) {
    ShoppingItem shoppingItem =
        new ShoppingItem(findOrCreateArticle(kkSupplier, content, ignoreBarcode), 0, false);
    double rawItemMultiplier =
        (shoppingItem.isWeighAble()
                ? getAsItemMultiplierAmount(content)
                : content.getContainerMultiplier() * content.getContainerSize())
            * -1;
    SupplyModel.checkFractionalItemMultiplier(rawItemMultiplier, content.getKkNumber());
    shoppingItem.setItemMultiplier((int) Math.round(rawItemMultiplier));
    shoppingItem.setOrderNo(orderNo);
    return shoppingItem;
  }

  private static double getAsItemMultiplierAmount(LineContent content) {
    return content
        .getUnit()
        .inUnit(
            MetricUnits.GRAM,
            content.getContainerMultiplier() * content.getContainerSize() * content.getAmount());
  }

  private static @NotNull Article createArticle(LineContent content, boolean ignoreBarcode, ShopRange shopRange) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Supplier kkSupplier = Supplier.getKKSupplier();
    Article pattern = ArticleRepository.nextArticleTo(em, content.getKkNumber(), kkSupplier);
    Article article = new Article();
    article.setSupplier(kkSupplier);
    article.setName(content.getName());
    article.setNetPrice(content.getPriceKb());
    article.setMetricUnits(content.getUnit());
    article.setAmount(content.getAmount());
    article.setProducer(content.getProducer());
    if (!ignoreBarcode) article.setBarcode(content.getBarcode());
    article.setWeighable(content.isWeighableKb());
    article.setContainerSize(content.getContainerSize());
    article.setShopRange(shopRange);
    article.setSurchargeGroup(pattern.getSurchargeGroup());
    VAT vat = content.getVat();
    if (vat == null) {
      vat = pattern.getVat();
    }
    article.setVat(vat);
    article.setPriceList(ArticleRepository.getValidPriceList(em, pattern));
    article.setVerified(false);
    article.setKbNumber(ArticleRepository.nextFreeKBNumber(em));
    article.setSuppliersItemNumber(content.getKkNumber());
    article.setSingleDeposit(content.getSingleDeposit());
    article.setContainerDeposit(content.getContainerDeposit());
    em.persist(article);
    em.flush();
    return article;
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return keyCapture.processKeyEvent(e) || barcodeCapture.processKeyEvent(e);
  }

  public String getPreorderCount(ShoppingItem t) {
    int preOrderCount = model.getPreorderCount(t.getSuppliersItemNumber());
    if (t.isWeighAble()) {
      return preOrderCount * Math.round(t.getContainerSize() * 1000) / 1000 + "KG";
    }
    return preOrderCount + " Gebinde";
  }
}
